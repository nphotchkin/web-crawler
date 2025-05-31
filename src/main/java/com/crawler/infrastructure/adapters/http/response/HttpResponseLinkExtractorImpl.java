package com.crawler.infrastructure.adapters.http.response;

import com.crawler.application.exception.WebCrawlerException;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
public class HttpResponseLinkExtractorImpl implements HttpResponseLinkExtractor {

    public HttpResponseLinkExtractionResult extractLinksHavingSameDomain(
            URI uri, HttpResponse<String> response, Set<String> ignorePathsInResponse
    ) {
        var validUris = new HashSet<URI>();
        var pattern = Pattern.compile("href\\s*=\\s*\"(.*?)\"", Pattern.CASE_INSENSITIVE);
        var matcher = pattern.matcher(response.body());

        var malformedPaths = new HashSet<String>();

        while (matcher.find()) {
            try {
                var path = matcher.group(1);
                if (isIgnoredPath(path, ignorePathsInResponse)) {
                    log.trace("Ignored path: '{}' as it matched ignored patterns Response Content for: '{}'", path, uri);
                    continue;
                }

                var optionalResolvedUri = tryResolveUri(uri, path);
                if (optionalResolvedUri.isEmpty()) {
                    malformedPaths.add(path);
                    log.debug("Ignored malformed path: '{}' while resolving Response Content for URI: '{}'", path, uri);
                    continue;
                }
                var resolvedUri = optionalResolvedUri.get();
                if (!resolvedUri.isOpaque()) {
                    var sanitizedLink = removeFragment(resolvedUri);
                    if (hasSameHostAndIsSameAsBaseUrl(uri, sanitizedLink)) {
                        validUris.add(sanitizedLink);
                    }
                } else {
                    log.trace("Ignored opaque link: '{}' while processing Response Content for URI: '{}'", resolvedUri, uri);
                }

            } catch (Exception e) {
                throw WebCrawlerException.shouldNeverHappen(
                        "Failed to extract links from uri: '%s', reason: '%s'".formatted(
                                uri, e.getMessage()
                        ), e
                );
            }
        }

        return new HttpResponseLinkExtractionResult(validUris, malformedPaths);
    }

    private Optional<URI> tryResolveUri(URI baseUri, String path) {
        try {
            URI pathUri = new URI(path);
            return Optional.of(baseUri.resolve(pathUri));
        } catch (URISyntaxException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private boolean isIgnoredPath(String uriString, Set<String> ignorePathsInResponse) {
        return ignorePathsInResponse.stream().anyMatch(uriString::startsWith);
    }

    private boolean hasSameHostAndIsSameAsBaseUrl(URI baseUrl, URI fullUrl) {
        return baseUrl.getHost() != null && isSameHost(baseUrl, fullUrl);
    }

    public static boolean isSameHost(URI mainDomain, URI url) {
        if (url.getHost() == null) return false;
        return url.getHost().equalsIgnoreCase(mainDomain.getHost());
    }

    private URI removeFragment(URI uri) {
        // Because websites can link to elements on the same page, it would be un-necessary / add duplication
        // If we scraped them too.
        try {
            return new URI(
                    uri.getScheme(),
                    uri.getAuthority(),
                    uri.getPath(),
                    uri.getQuery(),
                    null  // without the fragment
            );
        } catch (URISyntaxException e) {
            throw WebCrawlerException.shouldNeverHappen("Unable to remove fragment from URI: '%s'".formatted(uri), e);
        }
    }

}
