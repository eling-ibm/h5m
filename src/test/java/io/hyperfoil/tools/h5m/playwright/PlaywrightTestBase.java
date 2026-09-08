package io.hyperfoil.tools.h5m.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

/**
 * Base class for Playwright end-to-end tests.
 *
 * <p>Manages the Playwright / Browser / Page lifecycle with class-scoped
 * browser creation (one browser per test class) and per-test fresh
 * {@link BrowserContext} + {@link Page} (isolated cookies / local-storage).
 *
 * <h2>Configuration (system properties)</h2>
 * <ul>
 *   <li>{@code playwright.baseUrl}   – base URL of the running app, default {@code http://localhost:8080}</li>
 *   <li>{@code playwright.headless}  – run headless, default {@code true}</li>
 *   <li>{@code playwright.slowMo}    – add a delay (ms) between actions, useful for visual debugging, default {@code 0}</li>
 * </ul>
 *
 * <h2>Adding a new page object</h2>
 * <ol>
 *   <li>Create a class in {@code io.hyperfoil.tools.h5m.playwright.pages} that extends {@link io.hyperfoil.tools.h5m.playwright.pages.BasePage}.</li>
 *   <li>Expose typed locators and compound action methods on that class.</li>
 *   <li>Instantiate it in your IT with {@code new MyPage(page)}.</li>
 * </ol>
 *
 * <h2>Running the tests</h2>
 * <pre>{@code
 *   # Start the app first (in another terminal):
 *   ./mvnw quarkus:dev -Pweb
 *
 *   # Then run the Playwright ITs:
 *   ./mvnw verify -Dplaywright -Dplaywright.baseUrl=http://localhost:8080
 * }</pre>
 */
public abstract class PlaywrightTestBase {

    // ── Class-level (one browser per test class) ──────────────────────────────

    protected static Playwright playwright;
    protected static Browser browser;

    /**
     * When {@code true}, {@code @AfterEach} teardown in subclasses skips folder
     * deletion so you can inspect the app state after a test run.
     *
     * <p>Set via {@code -Dplaywright.keepFolders} (presence alone is enough;
     * {@code -Dplaywright.keepFolders=true} also works).
     */
    protected static final boolean KEEP_FOLDERS;

    static {
        String prop = System.getProperty("playwright.keepFolders", "true");
        KEEP_FOLDERS = Boolean.parseBoolean(prop.isBlank() ? "true" : prop);
    }

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        String headlessProp = System.getProperty("playwright.headless", "true");
        boolean headless = Boolean.parseBoolean(headlessProp.isBlank() ? "false" : headlessProp);
        String slowMoProp = System.getProperty("playwright.slowMo", "0");
        double slowMo = Double.parseDouble(slowMoProp.isBlank() ? "0" : slowMoProp);
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(headless)
                        .setSlowMo(slowMo));
    }

    @AfterAll
    static void closeBrowser() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    // ── Per-test (fresh context + page) ───────────────────────────────────────

    protected BrowserContext context;
    protected Page page;

    @BeforeEach
    void newContext() {
        context = browser.newContext(new Browser.NewContextOptions()
                .setBaseURL(baseUrl()));
        page = context.newPage();
    }

    @AfterEach
    void closeContext() {
        if (page != null) {
            page.close();
        }
        if (context != null) {
            context.close();
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Returns the base URL read from the {@code playwright.baseUrl} system
     * property, falling back to {@code http://localhost:8080}.
     */
    protected static String baseUrl() {
        return System.getProperty("playwright.baseUrl", "http://localhost:8080");
    }
}
