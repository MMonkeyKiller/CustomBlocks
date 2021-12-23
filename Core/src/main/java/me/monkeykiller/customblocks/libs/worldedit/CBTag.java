package me.monkeykiller.customblocks.libs.worldedit;

import com.sk89q.jnbt.Tag;

public class CBTag extends Tag {
    final String s;
    final Parser p;

    CBTag(Parser p, String s) {
        this.p = p;
        this.s = s;
    }

    public String getValue() {
        return this.s;
    }
}
