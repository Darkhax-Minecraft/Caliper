package net.darkhax.caliper.profiling.profilers.authors;

import java.util.Collection;

public class AuthorInfo implements Comparable<AuthorInfo> {

    private final String author;
    private final int modCount;
    private final Collection<String> mods;

    public AuthorInfo (String author, int modCount, Collection<String> mods) {

        this.author = author;
        this.modCount = modCount;
        this.mods = mods;
    }

    public String getAuthor () {

        return this.author;
    }

    public int getModCount () {

        return this.modCount;
    }

    public Collection<String> getMods () {

        return this.mods;
    }

    @Override
    public int compareTo (AuthorInfo o) {

        return Integer.compare(this.getModCount(), o.getModCount());
    }
}