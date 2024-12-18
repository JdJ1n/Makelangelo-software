# Tâche 3 Rapport

******

Projet : [Makelangelo](https://github.com/umontreal-diro/Makelangelo-software)

Membres : Jiadong Jin 20150692 - Yuxiang Lin 20172116

Répertoire : [Lien](https://github.com/JdJ1n/Makelangelo-software) vers le répertoire.

Documentation : Suivante.

Bonus : Le répertoire utilise le projet [lolcommits](https://github.com/lolcommits/lolcommits). ([Lien](https://github.com/JdJ1n/Makelangelo-software/tree/master/.lolcommits/Makelangelo) vers les captures)

******

## Les changements apportés à la Github action

Nous avons modifié le fichier `test.yml` en nous référant aux exemples que vous avez présentés en classe (le fichier [test.yml](https://github.com/umontreal-diro/cryptomator/blob/develop/.github/workflows/test.yml) dans le repo cryptomator) et en combinant nos connaissances sur l'utilisation des matrices. 
Nous avons défini et utilisé cinq flags JVM différents dans le workflow GitHub Actions pour effectuer les tâches de compilation et de test.
Nous avons utilisé `-XX:+PrintFlagsFinal` pour afficher tous les paramètres dans l'étape `Build and Test with Maven` et `-Xlog:gc` pour générer des journaux clairs, qui sont entièrement affichés dans l'étape `Display JVM Log`.

##### Avant

[Lien vers le fichier test.yml avant.](https://github.com/umontreal-diro/Makelangelo-software/blob/master/.github/workflows/test.yml)

##### Après

[Lien vers le fichier test.yml après.](https://github.com/JdJ1n/Makelangelo-software/blob/master/.github/workflows/test.yml)

### Common Flag

#### Source : `test.yml` ligne 20

#### JVM options : `-XX:+UnlockDiagnosticVMOptions -Xlog:gc*:file=jvm.log:time,level,tags -XX:+PrintFlagsFinal`

##### `-Xlog:gc*:file=jvm.log:time,level,tags`
- **Fonction :** Active la journalisation pour la collecte des déchets (Garbage Collection) et enregistre les journaux dans un fichier. 
  - `gc*` : Inclut toutes les journalisations liées à la collecte des déchets. 
  - `file=jvm.log` : Enregistre les journaux dans le fichier `jvm.log`. 
  - `time` : Ajoute des horodatages aux entrées du journal. 
  - `level` : Indique le niveau de chaque entrée du journal. 
  - `tags` : Ajoute des étiquettes pour faciliter le filtrage et l'analyse. 
- **Raison :** Fournit des informations détaillées et horodatées sur la collecte des déchets, ce qui est essentiel pour diagnostiquer les problèmes de performances et optimiser le comportement de la mémoire.
##### `-XX:+PrintFlagsFinal`
- **Fonction :** Affiche toutes les options et configurations finales de la JVM lors du démarrage. 
- **Raison :** Utile pour vérifier quelles options JVM sont effectivement appliquées et leurs valeurs finales, ce qui aide à valider la configuration et à résoudre les problèmes de configuration.

### Flag 1

#### Source : `test.yml` ligne 14

#### JVM options : `-Xms512m -Xmx1024m`

- **Fonction :** Définit la taille initiale du tas et la taille maximale du tas. 
- **Raison :** Assure que l'application dispose de suffisamment de mémoire au démarrage et limite l'utilisation maximale de la mémoire. 

### Flag 2

#### Source : `test.yml` ligne 15

#### JVM options : `-XX:+UseZGC`

- **Fonction :** Active le ramasse-miettes ZGC (Z Garbage Collector). 
- **Raison :** Conçu pour les applications nécessitant une faible latence et des performances élevées, ZGC est idéal pour minimiser les poses de collecte des déchets.

### Flag 3

#### Source : `test.yml` ligne 16

#### JVM options : `-Xmx256m -XX:+HeapDumpOnOutOfMemoryError`

- **Fonction :** Définit une petite taille maximale de tas et génère un vidage du tas en cas d'erreur OutOfMemoryError.
- **Raison :** Utilisé pour tester le comportement de l'application lorsque la mémoire est insuffisante, en fournissant des données de vidage pour l'analyse.

### Flag 4

#### Source : `test.yml` ligne 17

#### JVM options : `-XX:MaxMetaspaceSize=128m -XX:MaxNewSize=256m -XX:SurvivorRatio=128 -XX:MaxTenuringThreshold=0`

- **Fonction :** Ajuste la taille de l'espace metaspace, la taille maximale de la nouvelle génération, le ratio des espaces Survivor et le seuil de vieillissement maximal.
- **Raison :** Optimise la gestion de la mémoire en configurant finement les différentes zones de la mémoire.

### Flag 5

#### Source : `test.yml` ligne 18

#### JVM options : `-XX:+UseCompressedOops`

- **Fonction :** Active la compression des pointeurs d'objets.
- **Raison :** Réduit la consommation de mémoire et améliore l'efficacité, particulièrement bénéfique pour les systèmes 64 bits.

##### De nombreux efforts ont été déployés pour afficher les fichiers journaux appropriés dans leur intégralité. Parce que les logs, c'est la vie !!!