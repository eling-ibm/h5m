package io.hyperfoil.tools.h5m.playwright.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Page object for the Dashboard (root {@code /} route).
 *
 * <p>Exposes typed interactions with every element that tests need to drive.
 * Add new locators and action methods here as the dashboard gains features —
 * tests themselves should never contain raw CSS/XPath selectors.
 */
public class DashboardPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────

    public DashboardPage(Page page) {
        super(page);
    }

    /**
     * Scoped to the Carbon {@code <Content>} main area ({@code .cds--content})
     * to avoid matching elements in the hidden side-nav which also renders
     * folder names.
     */
    private Locator mainContent() {
        return page.locator(".cds--content");
    }

    /** Navigate to the dashboard and wait for it to finish loading. */
    public void open() {
        navigateTo("/");
    }

    // ── Heading ───────────────────────────────────────────────────────────────

    public Locator heading() {
        return page.getByRole(com.microsoft.playwright.options.AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Dashboard"));
    }

    // ── Summary cards ─────────────────────────────────────────────────────────

    public Locator foldersCard() {
        return page.getByText("Folders").first();
    }

    public Locator totalUploadsCard() {
        return page.getByText("Total Uploads");
    }

    public Locator changesDetectedCard() {
        return page.getByText("Changes Detected");
    }

    public Locator foldersWithChangesCard() {
        return page.getByText("Folders with Changes");
    }

    // ── Empty state ───────────────────────────────────────────────────────────

    public Locator emptyStateTile() {
        return page.getByText("No folders configured yet");
    }

    // ── Folder table ──────────────────────────────────────────────────────────

    /** The row for a named folder in the structured list.
     *
     * <p>Scoped to main content to avoid matching the hidden side-nav entry
     * that also renders each folder name.
     */
    public Locator folderRow(String folderName) {
        return mainContent().getByText(folderName);
    }

    // ── Toolbar actions ───────────────────────────────────────────────────────

    public Locator createFolderButton() {
        return page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Create Folder"));
    }

    // ── Create-Folder modal ───────────────────────────────────────────────────

    /** The folder name input inside the Create Folder modal. */
    public Locator folderNameInput() {
        return page.getByLabel("Folder name");
    }

    /** Primary submit button inside the Create Folder modal. */
    public Locator modalSubmitButton() {
        return page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Save"));
    }

    /** Cancel button inside the Create Folder modal. */
    public Locator modalCancelButton() {
        return page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Cancel"));
    }

    // ── Delete confirmation modal ─────────────────────────────────────────────

    /**
     * The primary "Delete" button inside the danger confirmation modal.
     *
     * <p>Carbon's {@code <Modal danger>} renders as a {@code role="dialog"}.
     * We scope to that dialog so we never accidentally match the trash-can
     * icon button (aria-label="Delete folder") still present in the main content.
     */
    public Locator deleteConfirmButton() {
        return page.getByRole(com.microsoft.playwright.options.AriaRole.DIALOG)
                .getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                        new Locator.GetByRoleOptions().setName("Delete"));
    }

    /** The trash-can (delete) icon button for a specific folder row.
     *
     * <p>Carbon's {@code StructuredListRow} with {@code selection} prop renders
     * each row as a {@code <label>}. The trash-can button carries
     * {@code aria-label="Delete folder"} (from {@code iconDescription}).
     * We locate the row by matching any element inside {@code .cds--content}
     * that contains the folder name text, then find the button by aria-label
     * within that row, regardless of whether the row element is a {@code <label>}
     * or a {@code <div>}.
     */
    public Locator deleteIconForFolder(String folderName) {
        return mainContent()
                .locator(".cds--structured-list-row, label")
                .filter(new Locator.FilterOptions().setHasText(folderName))
                .first()
                .getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                        new Locator.GetByRoleOptions().setName("Delete folder"));
    }

    // ── Compound actions ──────────────────────────────────────────────────────

    /**
     * Open the Create Folder modal, type {@code name}, and submit.
     * Waits for the modal to disappear before returning.
     */
    public void createFolder(String name) {
        createFolderButton().click();
        folderNameInput().fill(name);
        modalSubmitButton().click();
        waitForHidden(folderNameInput());
    }

    /**
     * Click the delete icon for {@code folderName}, then confirm in the modal.
     * Waits for the dialog to be visible before clicking "Delete" to avoid
     * a race between the click and Carbon's modal open animation.
     */
    public void deleteFolder(String folderName) {
        deleteIconForFolder(folderName).click();
        waitForVisible(deleteConfirmButton());
        deleteConfirmButton().click();
    }

    /**
     * Delete {@code folderName} only if it is still present in the list.
     * Safe to call from {@code @AfterEach} teardown — does nothing when the
     * folder was already deleted by the test itself.
     */
    public void tryDeleteFolder(String folderName) {
        if (folderRow(folderName).count() > 0) {
            deleteFolder(folderName);
        }
    }

    /**
     * Click a folder row to navigate to the FolderPage.
     * Returns the {@link FolderPage} so the caller can continue interacting.
     */
    public FolderPage openFolder(String folderName) {
        folderRow(folderName).first().click();
        return new FolderPage(page);
    }
}
