package io.hyperfoil.tools.h5m.playwright;

import io.hyperfoil.tools.h5m.playwright.pages.DashboardPage;
import io.hyperfoil.tools.h5m.playwright.pages.FolderPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * End-to-end Playwright tests for the Folder detail page
 * ({@code /folder/:folderId}).
 *
 * <p>Each test creates a fresh folder via the Dashboard and then navigates
 * to its detail page so tests remain independent of each other and of
 * pre-existing state.
 *
 * <p>Run with:
 * <pre>{@code
 *   ./mvnw verify -Dplaywright [-Dplaywright.baseUrl=http://localhost:8080]
 * }</pre>
 */
public class FolderPlaywrightIT extends PlaywrightTestBase {

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
        // Navigate back to the dashboard in case the test ended on a folder page.
        dashboardPage.open();
        dashboardPage.tryDeleteFolder(createdFolderName);
        createdFolderName = null;
    }

    // ── Tab bar ───────────────────────────────────────────────────────────────

    @Test
    void folderPage_showsFourTabs() {
        FolderPage folderPage = createFolderAndOpen("pw-tabs-" + System.currentTimeMillis());

        assertThat(folderPage.dataTab()).isVisible();
        assertThat(folderPage.nodesTab()).isVisible();
        assertThat(folderPage.graphTab()).isVisible();
        assertThat(folderPage.notificationTab()).isVisible();
    }

    @Test
    void dataTab_isSelectedByDefault() {
        FolderPage folderPage = createFolderAndOpen("pw-default-tab-" + System.currentTimeMillis());

        assertThat(folderPage.dataTab()).hasAttribute("aria-selected", "true");
    }

    @Test
    void nodesTab_switchesPanel() {
        FolderPage folderPage = createFolderAndOpen("pw-nodes-tab-" + System.currentTimeMillis());

        folderPage.clickNodesTab();

        // The "Create Node" button lives inside the Nodes panel
        assertThat(folderPage.createNodeButton()).isVisible();
    }

    @Test
    void graphTab_switchesPanel() {
        FolderPage folderPage = createFolderAndOpen("pw-graph-tab-" + System.currentTimeMillis());

        folderPage.clickGraphTab();

        assertThat(folderPage.graphCanvas()).isVisible();
    }

    @Test
    void notificationTab_switchesPanel() {
        FolderPage folderPage = createFolderAndOpen("pw-notif-tab-" + System.currentTimeMillis());

        folderPage.clickNotificationTab();

        // The "Create Notification Config" button lives inside the Notification panel
        assertThat(folderPage.createNotificationConfigButton()).isVisible();
    }

    // ── Nodes tab ─────────────────────────────────────────────────────────────

    @Test
    void nodesTab_showsEmptyStateWhenNoNodes() {
        FolderPage folderPage = createFolderAndOpen("pw-empty-nodes-" + System.currentTimeMillis());

        folderPage.clickNodesTab();

        assertThat(folderPage.nodesEmptyState()).isVisible();
    }

    @Test
    void createNode_appearsInList() {
        FolderPage folderPage = createFolderAndOpen("pw-create-node-" + System.currentTimeMillis());
        String nodeName = "pw-node-" + System.currentTimeMillis();

        folderPage.createJqNode(nodeName, ".value");

        assertThat(folderPage.nodeRow(nodeName)).isVisible();
    }

    @Test
    void deleteNode_removedFromList() {
        FolderPage folderPage = createFolderAndOpen("pw-del-node-" + System.currentTimeMillis());
        String nodeName = "pw-del-node-" + System.currentTimeMillis();

        folderPage.createJqNode(nodeName, ".value");
        assertThat(folderPage.nodeRow(nodeName)).isVisible();

        folderPage.deleteNode(nodeName);

        assertThat(folderPage.nodeRow(nodeName)).hasCount(0);
    }

    // ── Data tab ──────────────────────────────────────────────────────────────

    @Test
    void dataTab_uploadDataButtonVisible() {
        FolderPage folderPage = createFolderAndOpen("pw-data-tab-" + System.currentTimeMillis());

        // Data tab is active by default
        assertThat(folderPage.uploadDataButton()).isVisible();
    }

    // ── Notification tab ──────────────────────────────────────────────────────

    @Test
    void notificationTab_showsEmptyStateWhenNoConfigs() {
        FolderPage folderPage = createFolderAndOpen("pw-empty-notif-" + System.currentTimeMillis());

        folderPage.clickNotificationTab();

        assertThat(folderPage.notificationEmptyState()).isVisible();
    }

    @Test
    void notificationTab_createButtonIsVisible() {
        FolderPage folderPage = createFolderAndOpen("pw-notif-btn-" + System.currentTimeMillis());

        folderPage.clickNotificationTab();

        assertThat(folderPage.createNotificationConfigButton()).isVisible();
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    /**
     * Creates a folder via the dashboard, records its name for cleanup, and
     * returns a {@link FolderPage} already navigated to that folder.
     */
    private FolderPage createFolderAndOpen(String folderName) {
        createdFolderName = folderName;
        dashboardPage.createFolder(folderName);
        return dashboardPage.openFolder(folderName);
    }
}
