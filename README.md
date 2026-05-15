# pureboard
Tableau de bord simple


# structure du projet

```
projet {
    nom
    description
    repertoire
    fichierPom
    packageJson
    goMod
    cargoToml
    projetPom {
        nom
        parent {
            groupId
            artefactId
            version
        }
        artifact {
            groupId
            artefactId
            version
        }
        properties
        dependencies
        projetPomEnfants {...}
        projetNode {...}
        projetRust {...}
        projetGo {...}
    }
    modules
    projetNode {
        nom
        version
        script
        dependencies
        devDependencies
    }
    projetGit {
        lastCommit
        dateTimeLastCommit
        branches
        clean
        auteur
    }
    dateModification
    projetRust {
        nom
        version
    }
    projetGo {
        nom
        versionGo
    }
}
```
