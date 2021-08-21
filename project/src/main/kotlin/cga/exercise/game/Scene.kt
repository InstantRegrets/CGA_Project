package cga.exercise.game

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.DepthMap
import cga.exercise.components.geometry.GeometryBuffer
import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.Quad
import cga.exercise.components.light.PointLight
import cga.exercise.components.light.SpotLight
import cga.exercise.components.shader.*
import cga.exercise.components.shader.DepthShader
import cga.exercise.components.sound.SoundContext
import cga.exercise.components.sound.SoundListener
import cga.exercise.components.texture.Skybox
import cga.exercise.game.environment.Environment
import cga.exercise.game.gameObjects.GameObject
import cga.exercise.game.gameObjects.Phase
import cga.exercise.game.gameObjects.orb.Orb
import cga.exercise.game.gameObjects.player.Player
import cga.exercise.game.level.Level
import cga.exercise.game.gameObjects.note.NoteKey
import cga.framework.GLError
import cga.framework.GameWindow
import cga.framework.ModelLoader
import cga.framework.OBJLoader
import org.joml.Matrix3f
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL33.*
import kotlin.collections.ArrayList


/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(val window: GameWindow) {
    private var orbActualMesh: Mesh
    val camera: TronCamera
    private val level: Level
    private val player = Player()
    val gameObjects: MutableList<GameObject> = mutableListOf()
    private val orbs: MutableList<GameObject> = mutableListOf()

    val sun: Sun
    private val depthMap: DepthMap = DepthMap()
    private val depthShader = DepthShader(depthMap)

    //Deferred Shading Stuff
    private val gBuffer = GeometryBuffer(window)
    private val skybox = Skybox.invoke("assets/textures/skyboxNight")
    private val geometryPassShader: ShaderProgram
    private val stencilShader: ShaderProgram
    private val pointLightShader: ShaderProgram
    private val ambientEmitShader: ShaderProgram
    private val skyboxShader: ShaderProgram
    private val spotLightShader: ShaderProgram
    private val sphereMesh: Mesh
    private val pointLights: ArrayList<PointLight> = arrayListOf()
    private val spotLights: ArrayList<SpotLight> = arrayListOf()
    private val quad = Quad()
    private var phase = Phase.Day

    //Convenience
    private val width = window.windowWidth
    private val height = window.windowHeight

    init {
        //initial opengl state
        glClearColor(0f, 0f, 0f, 1.0f); GLError.checkThrow()
        glEnable(GL_CULL_FACE); GLError.checkThrow()
        glFrontFace(GL_CCW); GLError.checkThrow()
        glCullFace(GL_BACK); GLError.checkThrow()
        glEnable(GL_DEPTH_TEST); GLError.checkThrow()
        glDepthFunc(GL_LESS); GLError.checkThrow()
        stencilShader = StencilShader()
        pointLightShader = PointLightShader()
        geometryPassShader = GeometryShader()
        skyboxShader = SkyboxShader()
        ambientEmitShader = AmbientEmitShader()
        spotLightShader = SpotLightShader()

        // CAMERA
        camera = TronCamera()
        camera.translateLocal(Vector3f(7f,5f,40f))

        //Sound and level
        SoundContext.setup()
        level = Level()
        level.setup()
        skybox.setup()

        //Setup uniforms for lightingPass (Texture locations = output of gbuffer)
        pointLightShader.setup(width.toFloat(), height.toFloat())
        spotLightShader.setup(width.toFloat(), height.toFloat())
        ambientEmitShader.setup(width.toFloat(), height.toFloat())
        skyboxShader.setup()

        //Game Object creation
        sun = Sun(level.song.length)
        orbs.addAll(
            Orb.createOrbs(10, player)
        )
        gameObjects.addAll(
            listOf(
                Environment(),
                level, player, sun
            )
        )
        spotLights.add(sun.light)
        spotLights.add(level.lightShow.sceneLight)
        pointLights.addAll(orbs.map { (it as Orb).light })
        pointLights.add(player.light)
        gameObjects.addAll(orbs)

        //Sphere Mesh for Light Volumes
        val objMesh = ModelLoader.loadModel("assets/models/lightSphere.obj", 0f, 0f, 0f)
        sphereMesh = objMesh?.meshes?.first() ?: throw Exception("yeet")

        gameObjects.forEach { it.switchPhase(phase) }
        level.song.play()

        //WIP
        val orbMesh =ModelLoader.loadModel("assets/models/lightSphere.obj",0f,0f,0f)
        orbActualMesh = orbMesh?.meshes?.first() ?: throw Exception("yeet")
        val offsets = arrayListOf(
            Vector3f(-5f,1f,2f),
            Vector3f(-4f,0f,0f),
            Vector3f(-3f,0f,0f),
            Vector3f(-2f,0f,0f),
            Vector3f(-1f,0f,0f),
            Vector3f(0f,0f,0f),
            Vector3f(1f,0f,0f),
            Vector3f(2f,0f,0f),
            Vector3f(3f,0f,0f),
            Vector3f(4f,0f,0f),
            Vector3f(5f,0f,0f),
        )
        orbActualMesh.setupInstancing(offsets)
    }

    //RENDERING

    var rendStart: Long = 0L
    fun render(dt: Float, t: Float) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        deferredRender(dt, t)
        rendStart= System.nanoTime()
        renderSkybox()
        skyboxTime += (System.nanoTime() - rendStart)
        SoundListener.setPosition(camera)
        GLError.checkThrow()
        logFps()

    }

    private fun renderSkybox() {
        glEnable(GL_DEPTH_TEST)
        glDepthFunc(GL_LEQUAL) //prevent z-fighting
        skyboxShader.use()
        //We eliminate the translation part of the matrix
        val viewMatrix = Matrix4f(Matrix3f(camera.getCalculateViewMatrix()))
        //upload everything
        skyboxShader.setUniform("view_matrix", viewMatrix)
        skyboxShader.setUniform("projection_matrix", camera.getCalculateProjectionMatrix())
        //render :)
        skybox.render()
        //restore depth function
        glDepthFunc(GL_LESS)
    }

    private fun deferredRender(dt: Float, t: Float) {
        val beat = level.beatsPerSeconds * t
        gBuffer.startFrame()
        GLError.checkThrow()

        rendStart = System.nanoTime()
        geometryPass(dt, beat)
        geometryTime += (System.nanoTime()-rendStart); rendStart= System.nanoTime()
        ambientPass()
        ambientTime += (System.nanoTime()-rendStart); rendStart= System.nanoTime()

        spotLights.forEach {
            depthShader.pass(it, this, beat)
            spotLightPass(it) // todo second pass for mountain lights
        }
        spotLightTime += (System.nanoTime()-rendStart); rendStart= System.nanoTime()

        val viewLocal = camera.getCalculateViewMatrix()
        val projectionLocal = camera.getCalculateProjectionMatrix()

        glEnable(GL_STENCIL_TEST)
        pointLights.forEach {
            stencilPass(it, viewLocal, projectionLocal)
            pointLightPass(it, viewLocal, projectionLocal)
        }

        pointLightTime += (System.nanoTime()-rendStart); rendStart= System.nanoTime()

        glDisable(GL_STENCIL_TEST)


        copyDepthBuffer()
        GLError.checkThrow()

        finalPass()
        finalPassTime += (System.nanoTime()-rendStart); rendStart= System.nanoTime()
        GLError.checkThrow()
    }

    private fun spotLightPass(spotLight: SpotLight) {
        glEnable(GL_BLEND) //need to blend the volumes over one another
        glBlendEquation(GL_FUNC_ADD) //just straight up adding this, no calc required
        glBlendFunc(GL_ONE, GL_ONE) //equal rights for every pointLight!


        spotLightShader.use()
        gBuffer.bindForLightPass()
        spotLightShader.setUniform("CameraViewMatrix", camera.getCalculateViewMatrix())
        spotLightShader.setUniform("LightProjectionViewMatrix", spotLight.calcPVMatrix())
        glActiveTexture(GL_TEXTURE6)
        glBindTexture(GL_TEXTURE_2D, depthMap.texture)
        spotLight.bind(spotLightShader, camera.getCalculateViewMatrix())
        quad.draw(spotLightShader)
        glDisable(GL_BLEND)
    }

    private fun geometryPass(dt: Float, beat: Float) {
        geometryPassShader.use()
        gBuffer.bindForGeomPass()
        glDepthMask(true)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        glEnable(GL_DEPTH_TEST)

        geometryPassShader.setUniform("beat", beat)
        camera.bind(geometryPassShader)

        //Draw everything into gBuffer
        gameObjects.forEach { it.draw(geometryPassShader) }

        //geometryPassShader.setUniform("view_matrix", camera.getCalculateViewMatrix())
        //geometryPassShader.setUniform("projection_matrix", camera.getCalculateProjectionMatrix())
        //geometryPassShader.setUniform("model_matrix", Matrix4f())
        //orbActualMesh.renderInstanced(geometryPassShader, 11)

        glDepthMask(false)
        GLError.checkThrow("Geometry Pass")
    }

    private fun stencilPass(pointLight: PointLight, viewLocal: Matrix4f, projectionLocal: Matrix4f) {
        stencilShader.use()
        gBuffer.bindForStencilPass()
        //we want to run a depth test
        glEnable(GL_DEPTH_TEST)
        glDisable(GL_CULL_FACE) //we'll do that later
        glClear(GL_STENCIL_BUFFER_BIT) //Clear the stencil buffer (it's called for every pointLight)

        //we always want it to pass only depth matters (discard faces later)
        glStencilFunc(GL_ALWAYS, 0, 0)
        //Increase stencil value, if the back facing polygon fails the depth test, unchanged otherwise
        glStencilOpSeparate(GL_BACK, GL_KEEP, GL_INCR_WRAP, GL_KEEP)
        //decrease stencil value, if the front facing polygon fails the depth test, unchanged otherwise
        glStencilOpSeparate(GL_FRONT, GL_KEEP, GL_DECR_WRAP, GL_KEEP)
        //read more: https://ogldev.org/www/tutorial37/tutorial37.html


        //Calculate world view projection matrix and upload to shader for correct sphere rendering
        val mm = Matrix4f().translate(pointLight.getWorldPosition()).scale(pointLight.calcBoundingSphere())
        val wvp = calculateWVP(mm, viewLocal, projectionLocal)
        stencilShader.setUniform("wvp", wvp)
        sphereMesh.renderWOMat()
        GLError.checkThrow("Stencil Pass")
    }

    private fun pointLightPass(pointLight: PointLight, viewLocal: Matrix4f, projectionLocal: Matrix4f) {
        //we render into the final color attachment
        //also activates all textures from the gBuffer
        gBuffer.bindForLightPass()
        pointLightShader.use()

        glStencilFunc(GL_NOTEQUAL, 0, 0xFF) //fail test for everything != 0

        glDisable(GL_DEPTH_TEST) //dont need that anymore, it's already calculated
        glEnable(GL_BLEND) //need to blend the volumes over one another
        glBlendEquation(GL_FUNC_ADD) //just straight up adding this, no calc required
        glBlendFunc(GL_ONE, GL_ONE) //equal rights for every pointLight!

        glEnable(GL_CULL_FACE) //enable culling, so we can discard faces
        glCullFace(GL_FRONT)


        val mm = Matrix4f().translate(pointLight.getWorldPosition()).scale(pointLight.calcBoundingSphere())
        val wvp = calculateWVP(mm, viewLocal, projectionLocal)

        //upload world view projection matrix to shader for correct sphere rendering
        pointLightShader.setUniform("wvp", wvp)
        pointLight.bind(pointLightShader, viewLocal)
        //render the sphere without any materials, it's just to kick off the fragment shader
        sphereMesh.renderWOMat()
        //Light.bindAmount(lightingPassShader)
        GLError.checkThrow("light Pass")

        // restore state
        glCullFace(GL_BACK)
        glDisable(GL_BLEND)
    }

    fun ambientPass() {
        glEnable(GL_BLEND) //need to blend the volumes over one another
        glBlendEquation(GL_FUNC_ADD) //just straight up adding this, no calc required
        glBlendFunc(GL_ONE, GL_ONE) //equal rights for every pointLight!

        gBuffer.bindForLightPass()
        ambientEmitShader.use()
        quad.draw(ambientEmitShader)
        glDisable(GL_BLEND)
    }

    private fun finalPass() {
        //Copy gBuffer into default Framebuffer
        gBuffer.bindForFinalPass()
        glBlitFramebuffer(
            0, 0, width, height,
            0, 0, width, height,
            GL_COLOR_BUFFER_BIT, GL_LINEAR
        )
    }

    //we need to copy the depth buffer to render things like the skybox correctly
    private fun copyDepthBuffer() {
        //binds the gBuffer for reading, the default FBO for writing
        gBuffer.bindForDepthReadout()
        GLError.checkThrow()
        //copy the content of the gBuffers depth_buffer_bit to the default FBO
        glBlitFramebuffer(
            0, 0, width, height,
            0, 0, width, height,
            GL_DEPTH_BUFFER_BIT, GL_NEAREST
        )
        GLError.checkThrow()
        glBindFramebuffer(GL_FRAMEBUFFER, 0) //kinda useless, but who knows
    }

    //world view projection matrix calculation (to save uniforms)
    private fun calculateWVP(model: Matrix4f, view: Matrix4f, projection: Matrix4f): Matrix4f {
        return Matrix4f(projection).mul(view).mul(model)
    }

    //Framerate calculation
    private var startTime = System.nanoTime()
    private var frames = 0
    private var geometryTime = 0L
    private var ambientTime = 0L
    private var spotLightTime = 0L
    private var pointLightTime = 0L
    private var finalPassTime = 0L
    private var skyboxTime = 0L
    private fun logFps(){
        frames++
        if(System.nanoTime() - startTime >= 1000000000) {
            println("=======================")
            println("FPSCounter: fps $frames")
            println("avg geometry Pass:   ${geometryTime/frames}")
            println("avg ambient Pass:    ${ambientTime/frames}")
            println("avg spotlight Pass:  ${spotLightTime/frames}")
            println("avg pointLight Pass: ${pointLightTime/frames}")
            println("avg final PassTime:  ${finalPassTime/frames}")
            println("avg skybox PassTime: ${skyboxTime/frames}")
            frames = 0
            startTime = System.nanoTime()
            geometryTime = 0
            skyboxTime= 0
            ambientTime = 0
            pointLightTime = 0
            spotLightTime = 0
            finalPassTime = 0
        }
    }


    //GAME LOGIC

    fun update(dt: Float, t: Float) {
        val beat = level.beatsPerSeconds * t
        camera.update(dt,t)
        gameObjects.forEach{
            it.processInput(window, dt)
            it.update(dt, beat)
        }

        when {
            phase == Phase.Day          && t > level.song.length * 0.107f && t < level.song.length * 0.2f -> {
                switchPhase(Phase.Night,t)
            }
            phase == Phase.Night && t > level.song.length * 0.205 && t< level.song.length * 0.3f -> {
                switchPhase(Phase.Day,t)
            }
            phase == Phase.Day && t > level.song.length * 0.303f && t< level.song.length * 0.4f -> {
                switchPhase(Phase.Chaos,t)
            }
            phase == Phase.Chaos && t > level.song.length * 0.400 && t< level.song.length * 0.5f -> {
                switchPhase(Phase.Day,t)
            }
            phase == Phase.Day && t > level.song.length * 0.5f && t< level.song.length * 0.59f -> {
                switchPhase(Phase.Chaos,t)
            }
            phase == Phase.Chaos && t > level.song.length * 0.598f && t< level.song.length * 0.7f -> {
                switchPhase(Phase.Day,t)
            }
            phase == Phase.Day && t > level.song.length * 0.7f && t< level.song.length * 0.8f -> {
                switchPhase(Phase.Night,t)
            }
            phase == Phase.Night && t > level.song.length * 0.8f && t< level.song.length * 0.9f -> {
                switchPhase(Phase.Day,t)
            }
            phase == Phase.Day && t > level.song.length * 0.9f && t< level.song.length * 1.0f -> {
                switchPhase(Phase.Night,t)
            }
        }
    }
    private fun switchPhase(newPhase: Phase, beat: Float){
        this.phase = newPhase
        gameObjects.forEach { it.switchPhase(phase) }
        camera.switchPhase(phase,beat) //todo
    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {
        if (key == GLFW_KEY_F && action == 1) {
            level.onKey(NoteKey.Left)
        }
        if (key == GLFW_KEY_J && action == 1) {
            level.onKey(NoteKey.Right)
        }
    }

    var x: Double = width / 2.0
    var y: Double = height / 2.0
    fun onMouseMove(xPos: Double, yPos: Double) {
         if (x == 0.0) {
             x = xPos
         } else {
             val diff = (x - xPos) * 0.002
             x = xPos
             camera.rotateAroundPoint(0f, diff.toFloat(), 0f, Vector3f(0f,0f,0f))
         }
        // if (y == 0.0) {
        //     y = yPos
        // } else {
        //     val diff = (y - yPos) * 0.002
        //     y = yPos
        //     camera.camTarget.rotateAroundPoint(diff.toFloat(), 0f, 0f,  Vector3f(0f,0f,0f))
        // }
    }

    fun cleanup() {
        SoundContext.cleanup()
    }
}

