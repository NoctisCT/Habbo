/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.joran.spi;

public class HostClassAndPropertyDouble {
    final Class<?> hostClass;
    final String propertyName;

    public HostClassAndPropertyDouble(Class<?> hostClass, String propertyName) {
        this.hostClass = hostClass;
        this.propertyName = propertyName;
    }

    public Class<?> getHostClass() {
        return this.hostClass;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.hostClass == null ? 0 : this.hostClass.hashCode());
        result = 31 * result + (this.propertyName == null ? 0 : this.propertyName.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        HostClassAndPropertyDouble other = (HostClassAndPropertyDouble)obj;
        if (this.hostClass == null ? other.hostClass != null : !this.hostClass.equals(other.hostClass)) {
            return false;
        }
        return !(this.propertyName == null ? other.propertyName != null : !this.propertyName.equals(other.propertyName));
    }
}

