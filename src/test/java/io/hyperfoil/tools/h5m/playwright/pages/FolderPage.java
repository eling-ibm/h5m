package io.hyperfoil.tools.h5m.playwright.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Page object for the Folder detail page ({@code /folder/:folderId}).
 *
 * <p>The page contains four tabs — Data, Nodes, Graph, and Notification.
 * Each section's locators and actions are grouped by tab name. Add new
 * helpers here when new folder-level features are added to the UI.
 */
public class FolderPage extends BasePage {

    public FolderPage(Page page) {
        super(page);
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    public void open(long folderId) {
        navigateTo("/folder/" + folderId);
    }

    // ── Tab bar ───────────────────────────────────────────────────────────────

    public Locator dataTab() {
        return page.getByRole(com.microsoft.playwright.options.AriaRole.TAB,
                new Page.GetByRoleOptions().setName("Data"));
    }

    public Locator nodesTab() {
        return page.getByRole(com.microsoft.playwright.options.AriaRole.TAB,
                new Page.GetByRoleOptions().setName("Nodes"));
    }

    public Locator graphTab() {
        return page.getByRole(com.microsoft.playwright.options.AriaRole.TAB,
                new Page.GetByRoleOptions().setName("Graph"));
    }

    public Locator notificationTab() {
        return page.getByRole(com.microsoft.playwright.options.AriaRole.TAB,
                new Page.GetByRoleOptions().setName("Notification"));
    }

    public void clickDataTab() {
        dataTab().click();
    }

    public void clickNodesTab() {
        nodesTab().click();
    }

    public void clickGraphTab() {
        graphTab().click();
    }

    public void clickNotificationTab() {
        notificationTab().click();
    }

    // ── Data tab ──────────────────────────────────────────────────────────────

    public Locator uploadDataButton() {
        return page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Upload Data"));
    }

    /** The DataTable that shows uploaded runs on the Data tab. */
    public Locator dataTable() {
        return page.locator("[data-testid='data-table'], .cds--data-table").first();
    }

    // ── Nodes tab ─────────────────────────────────────────────────────────────

    public Locator createNodeButton() {
        return page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Create Node"));
    }

    public Locator nodesEmptyState() {
        return page.getByText("No nodes defined yet");
    }

    /** Row in the node structured list for a given node name. */
    public Locator nodeRow(String nodeName) {
        return page.getByRole(com.microsoft.playwright.options.AriaRole.ROW)
                .filter(new Locator.FilterOptions().setHasText(nodeName));
    }

    // ── Create Node modal ─────────────────────────────────────────────────────

    public Locator nodeNameInput() {
        return page.getByLabel("Name (required)");
    }

    public Locator nodeTypeSelect() {
        return page.getByLabel("Node type");
    }

    public Locator nodeOperationInput() {
        return page.getByLabel("Operation");
    }

    public Locator createNodeModalSubmit() {
        return page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Save"));
    }

    // ── Delete Node modal ─────────────────────────────────────────────────────

    public Locator deleteNodeConfirmButton() {
        return page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Delete"));
    }

    /** Action menu button for a specific node row. */
    public Locator nodeActionMenuButton(String nodeName) {
        return nodeRow(nodeName).getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new Locator.GetByRoleOptions().setName("Action"));
    }

    public Locator nodeDeleteMenuItem() {
        return page.getByRole(com.microsoft.playwright.options.AriaRole.MENUITEM,
                new Page.GetByRoleOptions().setName("Delete"));
    }

    public Locator nodeEditMenuItem() {
        return page.getByRole(com.microsoft.playwright.options.AriaRole.MENUITEM,
                new Page.GetByRoleOptions().setName("Edit"));
    }

    // ── Graph tab ─────────────────────────────────────────────────────────────

    /** The react-flow SVG container rendered by the graph visualiser. */
    public Locator graphCanvas() {
        return page.locator(".react-flow").first();
    }

    // ── Notification tab ──────────────────────────────────────────────────────

    public Locator createNotificationConfigButton() {
        return page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Create Notification Config"));
    }

    public Locator notificationEmptyState() {
        return page.getByText("No notifications configured");
    }

    /** Row in the notification structured list for a given method tag (e.g. "WEBHOOK"). */
    public Locator notificationRow(String method) {
        return page.getByRole(com.microsoft.playwright.options.AriaRole.ROW)
                .filter(new Locator.FilterOptions().setHasText(method));
    }

    // ── Create Notification modal ─────────────────────────────────────────────

    /** Method select inside the Create Notification Config modal. */
    public Locator notificationMethodSelect() {
        return page.getByLabel("Method");
    }

    public Locator createNotificationModalSubmit() {
        return page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Create"));
    }

    // ── Compound actions ──────────────────────────────────────────────────────

    /**
     * Switch to the Nodes tab and create a JQ node with the given name and operation.
     */
    public void createJqNode(String name, String operation) {
        clickNodesTab();
        createNodeButton().click();
        nodeNameInput().fill(name);
        nodeOperationInput().fill(operation);
        createNodeModalSubmit().click();
        waitForHidden(nodeNameInput());
    }

    /**
     * Delete the named node via the action menu in the Nodes tab.
     */
    public void deleteNode(String nodeName) {
        clickNodesTab();
        nodeActionMenuButton(nodeName).click();
        nodeDeleteMenuItem().click();
        deleteNodeConfirmButton().click();
    }
}
