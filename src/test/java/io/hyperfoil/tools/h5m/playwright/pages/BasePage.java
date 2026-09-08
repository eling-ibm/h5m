package io.hyperfoil.tools.h5m.playwright.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;

/**
 * Base page object. All page objects extend this class.
 *
 * <p>Encapsulates the Playwright {@link Page} and provides shared helpers
 * such as navigation, waiting, and common assertion utilities. Add new
 * shared interactions here so that subclass page objects stay focused on
 * their own selectors and actions.
 */
public abstract class BasePage {

    protected final Page page;

    protected BasePage(Page page) {
        this.page = page;
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    /**
     * Navigate to a path relative to the configured base URL and wait for the
     * network to become idle before returning.
     */
    public void navigateTo(String path) {
        page.navigate(path);
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    // ── Waiting helpers ───────────────────────────────────────────────────────

    /**
     * Wait until a locator is visible on screen.
     */
    public void waitForVisible(Locator locator) {
        locator.waitFor(new Locator.WaitForOptions().setState(
                com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
    }

    /**
     * Wait until a locator is detached / hidden.
     */
    public void waitForHidden(Locator locator) {
        locator.waitFor(new Locator.WaitForOptions().setState(
                com.microsoft.playwright.options.WaitForSelectorState.HIDDEN));
    }

    // ── Generic query ─────────────────────────────────────────────────────────

    /** Raw access to the underlying page for cases not covered by helpers. */
    public Page getPage() {
        return page;
    }
}
