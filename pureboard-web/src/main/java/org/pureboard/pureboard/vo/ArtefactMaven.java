package org.pureboard.pureboard.vo;

public record ArtefactMaven(String groupId, String artefactId, String version) {

    @Override
    public String toString() {
        return groupId + ':' + artefactId + ':' + version;
    }
}
