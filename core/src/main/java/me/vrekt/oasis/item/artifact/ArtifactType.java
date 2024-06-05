package me.vrekt.oasis.item.artifact;

import me.vrekt.oasis.item.artifact.artifacts.QuickStepArtifact;
import me.vrekt.oasis.item.artifact.artifacts.SecondWindArtifact;

/**
 * All artifact types
 */
public enum ArtifactType {

    QUICKSTEP {
        @Override
        public Artifact create() {
            return new QuickStepArtifact();
        }
    },
    SECOND_WIND {
        @Override
        public Artifact create() {
            return new SecondWindArtifact();
        }
    };

    public abstract Artifact create();

}
