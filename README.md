# Cayuga

![duck](https://upload.wikimedia.org/wikipedia/commons/thumb/4/45/Cayuga_drake_2012-05-02_001.jpg/1920px-Cayuga_drake_2012-05-02_001.jpg)

our spirit animal!

## Teammitglieder
- Simon Wöhler
- Bastian Abt
- Jannik Alexander



## Disclaimer: verwendete externe Inhalte

- [Duck sound](https://freesound.org/people/dobroide/sounds/185134/)
- [Bongo sound](https://freesound.org/people/stomachache/sounds/29803/)
- [Skybox Texture](https://assetstore.unity.com/packages/vfx/shaders/polyverse-skies-low-poly-skybox-shaders-104017)
- [Dirt Texture](https://www.vectorstock.com/royalty-free-vector/seamless-pattern-ground-with-stones-brown-soil-vector-37512397)

## Video

Aufgrund von Upload limits musste das Video auf Youtube hochgeladen werden.

[Cayuga_Featurevideo](https://www.youtube.com/watch?v=djm-R_fYwRw)

Da das Erklärvideo leider ein bisschen in der Bitrate eingebüßt hat, wir aber keine Zeit mehr hatten das komplett neu zu machen, haben wir noch einmal ein rohes Video hochgeladen, ohne Text o.ä.:

[Cayuga_Gameplay](https://www.youtube.com/watch?v=ygDq-dEilKE)

## Featureliste

Mini Disclaimer: Auch wenn nur ein name an einem Feature steht, wurde grundsätzlich zusammen Entwickelt (Ideen, Debugging, Unterstützung) :)

- Soundausgabe (musik, hit and miss sound) [Bastian]
- Einfaches rythmusspiel [Alle]
- Deferred Rendering [Jannik]
- Light Volume implementation für Point Lights [Jannik]
- Lichtshow durch Lasersystem [Simon]
- Shadowmapping der spotlights (Sonne und Laser) [Bastian]
- Point Shadows der Orbs [Bastian]
- Instancing [Jannik]
- Skybox Code [Simon + Jannik]
- Camera Shake und Kamerafahrten [Simon]
- Geometry shader abhängig von der Musik (pulse + vibe) [Bastian]
- Animierte Orbs, die aus mehreren Sphären bestehen, die um sich selber fliegen, die um den Spieler fliegen [Alle]
- Selbst modellierte(s)/colorierte(s):
  - Umgebung [Jannik]
  - Spielermodell [Simon]
  - Notenmodell [Simon]
  - Orbs (naja die Spheren nicht ;)) [Bastian]

**Kleinkram:**

- FPS-Logger (default off)
- Console hit Feedback
- chart loading von Beatsaver Charts
- Mehrere Phasen abhängig von der Musik 

**gestrichene Features:**
- ~~parametisierte Objekte~~
- ~~Toonshader + Fogshader~~
- ~~SSAO~~
- ~~Orbit + Flythrough Camera~~
- ~~Texture Matrizen~~ (animierte Texturen sind implementiert, werden aber nicht benutzt)


## Setup

- Level setup under [project/assets/levels](project/assets/levels)
- Graphik (shader) Quality under [project/src/main/kotlin/cga/framework/Quality.kt](project/src/main/kotlin/cga/framework/Quality.kt)
- run under [project/src/main/kotlin/cga/exercise/main.kt](project/src/main/kotlin/cga/exercise/main.kt)

## Controls

- **a** - Left Hit
- **s** - Both Hit
- **d** - Right Hit
- **l** - switch through render targets
    - skybox
    - ambient + emit
    - spotlights
    - pointlights
- **esc** - exit game

