/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.galaxy.metadata;

import com.google.common.base.Objects;

/**
 * The combination of a Galaxy tool id and version provides a unique tool reference.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 * @author <a href="mailto:y.hoogstrate@erasmusmc.nl">Youri Hoogstrate</a>
 */
public class ToolReference {
    private final String id;
    private final String version;

    public ToolReference(final String id, final String version) {
        this.id = id;
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return id + " " + version;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof ToolReference) {
            final ToolReference that = (ToolReference) obj;
            return Objects.equal(this.id, that.id) && Objects.equal(this.version, that.version);
        } else
            return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, version);
    }
}
