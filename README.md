# Cayuga

![duck](https://upload.wikimedia.org/wikipedia/commons/thumb/4/45/Cayuga_drake_2012-05-02_001.jpg/1920px-Cayuga_drake_2012-05-02_001.jpg)

our spirit animal!

# Teammitglieder
- Simon Wöhler
- Bastian Abt
- Jannik Alexander



# Disclaimer: verwendete externe Inhalte

- [Duck sound](https://freesound.org/people/dobroide/sounds/185134/)
- [Bongo sound](https://freesound.org/people/stomachache/sounds/29803/)
- [Skybox Texture](https://assetstore.unity.com/packages/vfx/shaders/polyverse-skies-low-poly-skybox-shaders-104017)
- [Dirt Texture](https://www.vectorstock.com/royalty-free-vector/seamless-pattern-ground-with-stones-brown-soil-vector-37512397)

# Featureliste

- Soundausgabe
- Einfaches rythmusspiel
- Deferred Rendering
- Light Volume implementation für Point Lights
- Lichtshow durch Lasersystem
- Shadowmapping der spotlights (Sonne und Laser)
- Point Shadows der Orbs
- Instancin
- Skybox Code
- Camera Shake und Kamera bewegung der Phasen
- Mehrere Phasen abhängig von der Musik
- Geometry shader abhängig von der Musik
- Animierte Orbs, die aus mehreren Spherers bestehen, die um sich selber fliegen, die um den Spieler fliegen
- Selbst modellierte(s)/colorierte(s):
  - Umgebung
  - Spielermodell
  - Notenmodell
  - Orbs (naja die Spheren nicht ;))

see [project README](project/README.md)

## Setup

- Level setup under [project/assets/levels/README.md](project/assets/levels/README.md)
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

