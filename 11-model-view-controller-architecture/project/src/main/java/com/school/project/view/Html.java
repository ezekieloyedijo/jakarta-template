package com.school.project.view;

/** VIEW helper: makes user-typed text safe to print in a page. (Topic 12 replaces this with <c:out>.) */
public final class Html {

    private Html() {
    }

    public static String escape(Object value) {
        if (value == null) return "";
        return value.toString().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
