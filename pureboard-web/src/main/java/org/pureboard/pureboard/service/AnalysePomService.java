package org.pureboard.pureboard.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.pureboard.pureboard.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class AnalysePomService {


    private static final Logger LOGGER = LoggerFactory.getLogger(AnalysePomService.class);


    public void analyseProjet(Projet projet) throws IOException {
        if (StringUtils.isNotBlank(projet.getFichierPom())) {
            Path pomFile = Path.of(projet.getFichierPom());

            analysePom(pomFile, projet);
        }

        if (StringUtils.isNotBlank(projet.getPackageJson())) {
            Path jsonFile = Path.of(projet.getPackageJson());

            ProjetNode resultat = new ProjetNode();
            analysePackageJson(jsonFile, resultat);
            projet.setProjetNode(resultat);
        }

//        if (StringUtils.isNotBlank(projet.getGoMod())) {
//            analyseGoMod(projet.getGoMod(), resultat);
//        }

//        if (StringUtils.isNotBlank(projet.getCargoToml())) {
//            analyseCargo(projet.getCargoToml(), resultat);
//        }
        Path pathGit = Path.of(projet.getRepertoire()).resolve(".git");
        if (Files.exists(pathGit)) {
            ProjetGit resultat = new ProjetGit();
            analyseGit(pathGit, resultat);
            projet.setProjetGit(resultat);
        }
    }

    public void analyseProjet2(Projet projet) throws IOException {
        if (StringUtils.isNotBlank(projet.getFichierPom())) {
            Path pomFile = Path.of(projet.getFichierPom());

            analysePom(pomFile, projet);
        }
    }

    private void analysePom(Path pomFile, Projet projet) throws IOException {
        if (pomFile != null) {
            ProjetPom projetPom = new ProjetPom();
            if (Files.exists(pomFile)) {

                MavenXpp3Reader reader = new MavenXpp3Reader();
                Model model;
                try (var fileReader = Files.newBufferedReader(pomFile)) {
                    model = reader.read(fileReader);
                } catch (Exception e) {
                    // Gérer les exceptions de lecture XML (par exemple, fichier mal formé)
                    throw new IOException("Erreur lors de la lecture du fichier POM : " + pomFile, e);
                }
                projet.setProjetPom(projetPom);

                if (model != null) {
                    projet.setDescription(model.getDescription());
                    if (model.getName() != null) {
                        projetPom.setNom(model.getName());
                    }
                    Parent parent = model.getParent();
                    if (parent != null) {
                        ArtefactMaven parentArtefact = new ArtefactMaven(parent.getGroupId(), parent.getArtifactId(), parent.getVersion());
                        projetPom.setParent(parentArtefact);

                    } else {

                    }
                    ArtefactMaven artefactMaven = new ArtefactMaven(model.getGroupId(), model.getArtifactId(), model.getVersion());
                    projetPom.setArtifact(artefactMaven);

                    if (!CollectionUtils.isEmpty(model.getProperties())) {
                        Map<String, String> map = new TreeMap<>();
                        model.getProperties().forEach((nom, valeur) -> {
                            if (valeur != null) {
                                map.put((String) nom, valeur.toString());
                            } else {
                                map.put((String) nom, "");
                            }
                        });
                        projetPom.setProperties(map);
                    }
                    if (!CollectionUtils.isEmpty(model.getDependencies())) {
                        List<ArtefactMaven> liste = new ArrayList<>();
                        model.getDependencies().forEach((dep) -> {
                            liste.add(new ArtefactMaven(dep.getGroupId(), dep.getArtifactId(), dep.getVersion()));
                        });
                        projetPom.setDependencies(liste);

                    }
                }

                // analyse des enfants

                var liste = Files.list(pomFile.getParent())
                        .filter(Files::isDirectory)
                        .toList();

                for (var f : liste) {
                    var f2 = f.resolve("pom.xml");
                    if (Files.exists(f2)) {
                        Projet projetEnfant = new Projet();
                        projetEnfant.setNom(f.getFileName().toString());
                        //rechercheRepertoireService.completeProjet(f, projetEnfant);
                        analysePom(f2, projetEnfant);
                        ProjetPom projetPom2 = null;
                        if (projetEnfant.getProjetPom() != null) {
                            if (projetPom.getProjetPomEnfants() == null) {
                                projetPom.setProjetPomEnfants(new ArrayList<>());
                            }
                            if (projetEnfant.getNom() != null && projetEnfant.getProjetPom().getNom() == null) {
                                projetEnfant.getProjetPom().setNom(projetEnfant.getNom());
                            }
                            projetPom2 = projetEnfant.getProjetPom();
                            projetPom.getProjetPomEnfants().add(projetPom2);
                        }

                        var f3 = f.resolve("package.json");
                        if (Files.exists(f3)) {
                            ProjetNode resultat2 = new ProjetNode();
                            analysePackageJson(f3, resultat2);
                            projetEnfant.setProjetNode(resultat2);
                            if (projetPom2 == null) {
                                projetPom2 = new ProjetPom();
                                projetPom2.setNom(projetEnfant.getNom());
                                projetPom2.getProjetPomEnfants().add(projetPom2);
                            }
                            projetPom2.setProjetNode(resultat2);
                        }
                    }
                }

            }
        }
    }


    private void analysePackageJson(Path jsonFile, ProjetNode resultat) throws IOException {

        if (Files.exists(jsonFile)) {

            ObjectMapper mapper = new ObjectMapper();

            try (var reader = Files.newBufferedReader(jsonFile)) {
                JsonNode node = mapper.reader().readTree(reader);

                if (node.has("name")) {
                    resultat.setNom(node.get("name").asText());
                }
                if (node.has("version")) {
                    resultat.setVersion(node.get("version").asText());
                }
                if (node.has("scripts")) {
                    Map<String, String> map = new TreeMap<>();
                    for (var script : node.get("scripts").properties()) {
                        map.put(script.getKey(), script.getValue().asText());
                    }
                    resultat.setScript(map);
                }
                if (node.has("dependencies")) {
                    Map<String, String> map = new TreeMap<>();
                    for (var script : node.get("dependencies").properties()) {
                        map.put(script.getKey(), script.getValue().asText());
                    }
                    resultat.setDependencies(map);
                }
                if (node.has("devDependencies")) {
                    Map<String, String> map = new TreeMap<>();
                    for (var script : node.get("devDependencies").properties()) {
                        map.put(script.getKey(), script.getValue().asText());
                    }
                    resultat.setDevDependencies(map);
                }
            }

        }

    }


    private void analyseGit(Path pathGit, ProjetGit resultat) {
        try (Repository repository = new RepositoryBuilder().setGitDir(pathGit.toFile()).readEnvironment().findGitDir().build()) {

            Git git = new Git(repository);
            RevCommit latestCommit = git.
                    log().
                    setMaxCount(1).
                    call().
                    iterator().
                    next();

            String latestCommitHash = latestCommit.getName();
            resultat.setIdCommitComplet(latestCommitHash);
            resultat.setIdCommit(latestCommitHash.substring(0, 7));
            resultat.setMessage(latestCommit.getFullMessage());
            resultat.setBranche(repository.getFullBranch());
            var date = Instant.ofEpochSecond(latestCommit.getCommitTime());
            resultat.setDate(LocalDateTime.ofInstant(date, ZoneOffset.systemDefault()));

            Status status = git.status().call();
            if (!status.isClean()) {
                resultat.setFichiersNonTracke(List.of(status.getUntracked().toArray(new String[0])));
                resultat.setFichiersNonCommite(List.of(status.getModified().toArray(new String[0])));
            } else {
                resultat.setFichiersNonCommite(List.of());
                resultat.setFichiersNonTracke(List.of());
            }

            resultat.setListeBranchesCommit(new ArrayList<>());
            ObjectId head = repository.resolve("HEAD");
            try (RevWalk walk = new RevWalk(repository)) {
                RevCommit commit = walk.parseCommit(head);

                List<Ref> branches = git.branchList()
                        .setListMode(ListBranchCommand.ListMode.ALL)
                        .call();

                LOGGER.info("Branches contenant le commit courant :");
                for (Ref ref : branches) {
                    boolean contains = git.branchList()
                            .setContains(commit.getName())
                            .call()
                            .stream()
                            .anyMatch(r -> r.getName().equals(ref.getName()));

                    if (contains) {
                        LOGGER.info("  {}", ref.getName());
                        resultat.getListeBranchesCommit().add(ref.getName());
                    }
                }
            }

        } catch (Exception e) {
            LOGGER.error("Erreur lors de l'analyse du projet {}", pathGit, e);
        }
    }

}
