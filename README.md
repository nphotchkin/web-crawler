# Web Crawler
---

Recursively visits sites on the same host, with a fixed sized Thread Pool backed by a blocking queue to provide back pressure.

- [System Design Diagram](./docs/system-design.excalidraw) - open with [Excalidraw](https://excalidraw.com/)


⚠️ Note Well

- `App.class` contains a hardcoded property `limit` you can change for convenience, by default its set to `100`.

Outputs a report including all valid sites visited and any failed. Intentionally using the standard library where possible, some utility libraries such as [resilience4j](https://resilience4j.readme.io/docs/getting-started), [jobrunr](https://www.jobrunr.io/en/), [spring boot](https://spring.io/projects/spring-boot) could have been used to get more with the trade-off of adding dependencies.

# Requirements

- Java 21
- Apache Maven

## Run:

```
    mvn clean install
    mvn exec:java -Dexec.mainClass="com.crawler.App"
```

## Run Tests:

```
   mvn clean test
```

## Docker:

```
  docker build -t web-crawler .
  docker run --rm web-crawler
```

# Limitations & Notes

## Noteworthy Limitations

- Only supports server side generated links.
- False positives for malformed when page contains commented out links.
- Missing externalised config.

## Edge Cases Covered

During development, several edge cases were handled to improve crawl stability and avoid unnecessary or invalid requests:

- **Ignored Fragment Links**  
  URLs containing only fragments (e.g., `#section1`) or pointing to the same page are skipped since they don't lead to new content.

- **Ignored Opaque Links**  
  Non-HTTP/HTTPS links (like `mailto:`, `tel:`) are ignored, as they don't represent actual navigable content.

- **Malformed Links**  
  Handled malformed links, some are false positives due to the approach of using regex match to capture href's rather than parsing HTML (e.g. commented links be crawled).

# Trade-offs

- Externalised config not implemented to save time.
- Attempt was made to provide sensible defaults for Thread pool, this means it's not the fastest it could be but aims to be more stable.
- `CrawlerLinkExtractor` - Could be improved to retry failed failed requests on an exponential backoff instead it logs a warning message.
- `BasicHttpClient` - only supports request types required to fulfill Use Cases.
- Uses hard coded list for paths not to crawl instead you could parse `robots.txt` if present.
- Site map wasn't used to support Crawling Use Case.
- Attempt was made to isolate complexity of con-currency however the implementation could probably be simplified.
- No mechanism give up completely (e.g. bulk head) if lots of requests fail its just going to keep Crawling.
- Crawling task is not resumable if you terminate the program and restart it then the crawler will start over.
- Needs more testing to be considered a 'production' solution.
