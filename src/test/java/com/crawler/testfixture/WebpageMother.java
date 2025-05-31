package com.crawler.testfixture;

public class WebpageMother {

    public static class ValidSiteMapWithoutProblematicLinks {

        public static String indexPage() {
            return """
                    <!DOCTYPE html>
                    <html lang="en">
                    <head>
                      <meta charset="UTF-8">
                      <title>Index</title>
                    </head>
                    <body>
                      <h1>Welcome</h1>
                      <a href="/valid-link/blog">Valid Link</a>
                    </body>
                    </html>
            """;
        }

        public static String pageLinkedToFromIndex() {
            return """
                <!DOCTYPE html>
                    <html lang="en">
                    <head>
                      <meta charset="UTF-8">
                      <title>Valid Link Page</title>
                    </head>
                    <body>
                        <h1>Welcome to the valid link page!</h1>
                        <a href="/">Home</a>
                    </body>
                </html>
            """;
        }

    }

    public static class ValidSiteMapWithProblematicLinks {

        public static String indexPage() {
            return """
                    <!DOCTYPE html>
                    <html lang="en">
                    <head>
                      <meta charset="UTF-8">
                      <title>Index</title>
                    </head>
                    <body>
                      <h1>Welcome</h1>
                      <a href="/valid-link/blog">Blog Post</a>
                      <a href="/help/ monzo-pensions-transfers">Problematic Link</a> <!-- problematic-link -->
                    </body>
                    </html>
            """;
        }

        public static String pageLinkedToFromIndex() {
            return """
                    <!DOCTYPE html>
                    <html lang="en">
                    <head>
                      <meta charset="UTF-8">
                      <title>Blog Post</title>
                    </head>
                    <body>
                      <h1>Welcome to Blog post</h1>
                    </body>
                    </html>
            """;
        }

    }

    public static class ValidSiteMapWithSubDomainLinks {

        public static String indexPage() {
            return """
                    <!DOCTYPE html>
                    <html lang="en">
                    <head>
                      <meta charset="UTF-8">
                      <title>Index</title>
                    </head>
                    <body>
                      <h1>Welcome</h1>
                      <a href="/valid-link/blog">Blog Post</a>
                      <a href="https://www.monzo.support.com/sub-domain-link">Blog Post</a>
                    </body>
                    </html>
            """;
        }

        public static String pageLinkedToFromIndex() {
            return """
                    <!DOCTYPE html>
                    <html lang="en">
                    <head>
                      <meta charset="UTF-8">
                      <title>Blog Post</title>
                    </head>
                    <body>
                      <h1>Welcome to Blog post</h1>
                        <a href="https://www.monzo.support.com/sub-domain-link2">Blog Post</a>
                    </body>
                    </html>
            """;
        }

    }

    public static class SiteHavingBannedPaths {

        public static String indexPage() {
            return """
                    <!DOCTYPE html>
                    <html lang="en">
                    <head>
                      <meta charset="UTF-8">
                      <title>Index</title>
                    </head>
                    <body>
                      <h1>Welcome</h1>
                      <a href="/banned/">banned</a>
                      <a href="/banned/ban">banned ban</a>
                    </body>
                    </html>
            """;
        }
    }

}
