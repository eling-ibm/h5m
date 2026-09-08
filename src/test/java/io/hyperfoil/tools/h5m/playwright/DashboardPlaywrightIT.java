package io.hyperfoil.tools.h5m.playwright;

import io.hyperfoil.tools.h5m.playwright.pages.DashboardPage;
import io.hyperfoil.tools.h5m.playwright.pages.FolderPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * End-to-end Playwright tests for the Dashboard page ({@code /}).
 *
 * <p>Each test navigates to a fresh page and exercises one user-visible
 * behaviour. Tests are independent: each gets an isolated {@link
 * com.microsoft.playwright.BrowserContext} from {@link PlaywrightTestBase}.
 *
 * <p>These are integration tests — they require a running h5m instance.
 * Run with:
 * <pre>{@code
 *   ./mvnw verify -Dplaywright [-Dplaywright.baseUrl=http://localhost:8080]
 * }</pre>
 */
public class DashboardPlaywrightIT extends PlaywrightTestBase {

    private DashboardPage dashboardPage;
    /** Folder created during the test; deleted in {@link #tearDown()} if still present. */
    private String createdFolderName;

    @BeforeEach
    void setUp() {
        dashboardPage = new DashboardPage(page);
        dashboardPage.open();
    }

    @AfterEach
    void tearDown() {
        if (createdFolderName == null || KEEP_FOLDERS) {
            return;
        }
        // Navigate back to the dashboard in case the test ended on a different page.
        dashboardPage.open();
        dashboardPage.tryDeleteFolder(createdFolderName);
        createdFolderName = null;
    }

    // ── Layout ────────────────────────────────────────────────────────────────

    @Test
    void pageTitle_showsDashboard() {
        assertThat(dashboardPage.heading()).isVisible();
    }

    @Test
    void createFolderButton_isVisible() {
        assertThat(dashboardPage.createFolderButton()).isVisible();
    }

    // ── Empty state ───────────────────────────────────────────────────────────

    @Test
    void emptyState_shownWhenNoFolders() {
        // Assumes a clean instance with no folders; if the app already has folders
        // this test is skipped. See also DashboardPage#createFolder for setup.
        if (dashboardPage.folderRow("").count() == 0) {
            assertThat(dashboardPage.emptyStateTile()).isVisible();
        } else {
            System.out.println("[SKIP] emptyState_shownWhenNoFolders: Folders already exist, empty state not checked");
        }
    }

    // ── Create folder ─────────────────────────────────────────────────────────

    @Test
    void createFolder_appearsInList() {
        createdFolderName = "pw-test-folder-" + System.currentTimeMillis();
        dashboardPage.createFolder(createdFolderName);
        assertThat(dashboardPage.folderRow(createdFolderName).first()).isVisible();
        assertThat(dashboardPage.foldersCard()).isVisible();
        // Check Summary cards
        assertThat(dashboardPage.totalUploadsCard()).isVisible();
        assertThat(dashboardPage.changesDetectedCard()).isVisible();
        assertThat(dashboardPage.foldersWithChangesCard()).isVisible();
    }

    @Test
    void createFolder_cancelDoesNotCreate() {
        dashboardPage.createFolderButton().click();
        assertThat(dashboardPage.folderNameInput()).isVisible();
        dashboardPage.folderNameInput().fill("should-not-be-created");
        dashboardPage.modalCancelButton().click();
        assertThat(dashboardPage.folderNameInput()).not().isVisible();
    }

    // ── Delete folder ─────────────────────────────────────────────────────────

    @Test
    void deleteFolder_removedFromList() {
        createdFolderName = "pw-delete-" + System.currentTimeMillis();
        dashboardPage.createFolder(createdFolderName);
        assertThat(dashboardPage.folderRow(createdFolderName).first()).isVisible();

        dashboardPage.deleteFolder(createdFolderName);
        assertThat(dashboardPage.folderRow(createdFolderName)).hasCount(0);
        // folder is already gone — tearDown will find nothing to delete
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    @Test
    void clickFolderRow_navigatesToFolderPage() {
        createdFolderName = "pw-nav-" + System.currentTimeMillis();
        dashboardPage.createFolder(createdFolderName);

        FolderPage folderPage = dashboardPage.openFolder(createdFolderName);
        folderPage.waitForVisible(folderPage.dataTab());
        assertThat(folderPage.dataTab()).isVisible();
    }
}
