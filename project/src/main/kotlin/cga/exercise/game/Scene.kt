package cga.exercise.game

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.GeometryBuffer
import cga.exercise.components.geometry.Mesh
import cga.exercise.components.light.Light
import cga.exercise.components.light.PointLight
import cga.exercise.components.shader.*
import cga.exercise.components.sound.SoundContext
import cga.exercise.components.sound.SoundListener
import cga.exercise.components.texture.Skybox
import cga.exercise.game.environment.chaos.Environment
import cga.exercise.game.gameObjects.GameObject
import cga.exercise.game.gameObjects.orb.Orb
import cga.exercise.game.gameObjects.player.Player
import cga.exercise.game.level.Level
import cga.exercise.game.note.NoteKey
import cga.framework.GLError
import cga.framework.GameWindow
import cga.framework.ModelLoader
import org.joml.Matrix3f
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL30.*
import kotlin.math.PI
import kotlin.math.sin


/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {
    //Game Stuff
    private val camera: TronCamera
    private val level: Level
    private val player = Player()
    private val gameObjects: MutableList<GameObject> = mutableListOf()
    private val orbs: MutableList<GameObject> = mutableListOf()

    //Deferred Shading Stuff
    private val gBuffer = GeometryBuffer(window)
    private val skybox = Skybox.invoke("assets/textures/skyboxNight")
    private val geometryPassShader: ShaderProgram
    private val stencilShader: ShaderProgram
    private val lightingPassShader: ShaderProgram
    private val skyboxShader: ShaderProgram
    private val sphereMesh: Mesh
    private val pointLights: ArrayList<PointLight> = arrayListOf()
    private val quad = Quad()

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
        lightingPassShader = LightingPassShader()
        geometryPassShader = GeometryShader()
        skyboxShader = SkyboxShader()

        // CAMERA
        camera = TronCamera()
        camera.rotateLocal(-0.65f, 0.0f, 0f)
        camera.translateLocal(Vector3f(0f, 0f, 4f))
        camera.parent = player.model.renderable

        //Sound and level
        SoundContext.setup()
        level = Level()
        level.setup()
        skybox.setup()

        //Setup uniforms for lightingPass (Texture locations = output of gbuffer)
        lightingPassShader.setup(width.toFloat(), height.toFloat())
        skyboxShader.setup()

        //Game Object creation
        orbs.addAll(
            listOf(
                Orb(), Orb(), Orb(), Orb(), Orb(), Orb(), Orb(), Orb(), Orb(), Orb()
            )
        )
        gameObjects.addAll(
            listOf(
                Environment(),
                level, player
            )
        )
        pointLights.addAll(orbs.map { (it as Orb).light })
        pointLights.add(player.lighting.pointLight)
        gameObjects.addAll(orbs)
        //Sphere Mesh for Light Volumes
        val objMesh = ModelLoader.loadModel("assets/models/lightSphere.obj",0f,0f,0f)
        sphereMesh = objMesh?.meshes?.first() ?: throw Exception("yeet")
    }

    fun render(dt: Float, t: Float) {
        deferredRender(dt, t)
        GLError.checkThrow()
        //renderSkybox()
        SoundListener.setPosition(camera)
        GLError.checkThrow()
    }

    private fun renderSkybox() {
        glDepthFunc(GL_LEQUAL)
        skyboxShader.use()
        //We eliminate the translation part of the matrix
        val viewMatrix = Matrix4f(Matrix3f(camera.getCalculateViewMatrix()))
        skyboxShader.setUniform("view_matrix", viewMatrix)
        skyboxShader.setUniform("projection_matrix", camera.getCalculateProjectionMatrix())
        skybox.render()
        glDepthFunc(GL_LESS)
    }

    private fun deferredRender(dt: Float, t: Float){
        //TODO calc FPS

        gBuffer.startFrame()
        GLError.checkThrow()

        geometryPass(dt, t)

        glEnable(GL_STENCIL_TEST)

        pointLights.forEach {
            stencilPass(it)
            pointLightPass(it)

        }

        glDisable(GL_STENCIL_TEST)
        //TODO Directional Light

        finalPass()
        GLError.checkThrow()

        //copyDepthBuffer()
        GLError.checkThrow()

        //TODO Render FPS
    }

    private fun geometryPass(dt: Float, t:Float){
        geometryPassShader.use()
        gBuffer.bindForGeomPass()
        glDepthMask(true)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        glEnable(GL_DEPTH_TEST)

        val beat = level.beatsPerSeconds * t
        geometryPassShader.setUniform("beat",beat)
        camera.bind(geometryPassShader)
        //Draw everything into gBuffer
        gameObjects.forEach{it.draw(geometryPassShader)}

        glDepthMask(false)
        GLError.checkThrow("Geometry Pass")
    }
    private fun stencilPass(pointLight: PointLight) {
        stencilShader.use()
        gBuffer.bindForStencilPass()
        glEnable(GL_DEPTH_TEST)
        glDisable(GL_CULL_FACE)
        glClear(GL_STENCIL_BUFFER_BIT)

        //we always want it to pass only depth matters
        glStencilFunc(GL_ALWAYS,0,0)
        //Increase or decrease back / front in case of failed depth test
        glStencilOpSeparate(GL_BACK, GL_KEEP, GL_INCR_WRAP, GL_KEEP)
        glStencilOpSeparate(GL_FRONT, GL_KEEP, GL_DECR_WRAP, GL_KEEP)
        //Calculate model matrix for shader
        val viewLocal = camera.getCalculateViewMatrix()
        val projectionLocal = camera.getCalculateProjectionMatrix()
        val mm = Matrix4f().translate(pointLight.getWorldPosition()).scale(pointLight.calcBoundingSphere())

        stencilShader.setUniform("view", viewLocal)
        stencilShader.setUniform("proj", projectionLocal)
        stencilShader.setUniform("model", mm)
        sphereMesh.renderWOMat()
        GLError.checkThrow("Stencil Pass")
    }

    private fun pointLightPass(pointLight: PointLight){

        gBuffer.bindForLightPass()
        lightingPassShader.use()

        glStencilFunc(GL_NOTEQUAL, 0, 0xFF);

        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_ONE, GL_ONE);

        glEnable(GL_CULL_FACE);
        glCullFace(GL_FRONT);

        val viewLocal = camera.getCalculateViewMatrix()
        val projectionLocal = camera.getCalculateProjectionMatrix()
        val mm = Matrix4f().translate(pointLight.getWorldPosition()).scale(pointLight.calcBoundingSphere())

        lightingPassShader.setUniform("view1", viewLocal)
        lightingPassShader.setUniform("proj1", projectionLocal)
        pointLight.bind(lightingPassShader, viewLocal)
        lightingPassShader.setUniform("model1", mm)

        sphereMesh.renderWOMat()
        Light.bindAmount(lightingPassShader)
        GLError.checkThrow("lightPass")


        glCullFace(GL_BACK)
        glDisable(GL_BLEND)

    }

    private fun finalPass() {
        //Copy gBuffer into default Framebuffer
        gBuffer.bindForFinalPass()
        glBlitFramebuffer(
            0,0, width, height,
            0,0, width, height,
            GL_COLOR_BUFFER_BIT, GL_LINEAR)
    }

    //Experimental DOES cause bugs
    private fun copyDepthBuffer(){
        gBuffer.bindForDepthReadout()
        //copy the depth buffer from gbuffer to default
        glBlitFramebuffer(
            0,0, window.windowWidth, window.windowHeight,
            0,0, window.windowWidth, window.windowHeight,
            GL_DEPTH_BUFFER_BIT, GL_NEAREST
        )
        GLError.checkThrow()
        glBindFramebuffer(GL_FRAMEBUFFER, 0) //set both to default again
    }

    //world view projection matrix calculation
    //might cause bugs!
    private fun calculateWVP(model: Matrix4f, view: Matrix4f, projection: Matrix4f): Matrix4f{
        return Matrix4f(projection).mul(view).mul(model)
    }

    fun rainbow(vect: Vector3f, p: Float) {
        val r = (sin(p * 2 * PI + 0 / 3.0 * PI) / 2 + 0.5).toFloat()
        val g = (sin(p * 2 * PI + 2 / 3.0 * PI) / 2 + 0.5).toFloat()
        val b = (sin(p * 2 * PI + 4 / 3.0 * PI) / 2 + 0.5).toFloat()
        vect.x = r
        vect.y = g
        vect.z = b
    }

    fun update(dt: Float, t: Float) {
        val beat = level.beatsPerSeconds * t
        gameObjects.forEach{
            it.processInput(window, dt)
            it.update(dt, beat)
        }
        orbs.forEach {
            it.update(dt, beat)
        }
    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {
        if (key == GLFW_KEY_F && action == 1) {
            level.onKey(NoteKey.Left)
        }
        if (key == GLFW_KEY_J && action == 1) {
            level.onKey(NoteKey.Right)
        }
    }

    var x: Double = 0.0
    var y: Double = 0.0
    fun onMouseMove(xPos: Double, yPos: Double) {
        if (x == 0.0) {
            x = xPos
        } else {
            val diff = (x - xPos) * 0.002
            x = xPos
            camera.rotateAroundPoint(0f, diff.toFloat(), 0f, Vector3f(0f,0f,0f))
        }
          if (y == 0.0) {
              y = yPos
          } else {
              val diff = (y - yPos) * 0.002
              y = yPos
              camera.camTarget.rotateAroundPoint(diff.toFloat(), 0f, 0f,  Vector3f(0f,0f,0f))
          }
    }

    fun cleanup() {
        //level.cleanup()
        SoundContext.cleanup()
    }
}

