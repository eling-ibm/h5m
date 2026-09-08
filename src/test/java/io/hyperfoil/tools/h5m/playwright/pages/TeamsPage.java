package io.hyperfoil.tools.h5m.playwright.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Page object for the Teams page ({@code /teams}).
 *
 * <p>The page shows a two-column layout: a list of teams on the left and a
 * member management panel on the right once a team is selected. Add new
 * locators and action methods here as team management features expand.
 */
public class TeamsPage extends BasePage {

    public TeamsPage(Page page) {
        super(page);
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    public void open() {
        navigateTo("/teams");
    }

    // ── Teams panel (left column) ─────────────────────────────────────────────

    /** "Select a team" info notification shown before any team is selected. */
    public Locator selectTeamNotification() {
        return page.getByText("Select a team to manage members");
    }

    /** A team entry in the teams list identified by its name. */
    public Locator teamEntry(String teamName) {
        return page.getByText(teamName);
    }

    /** The "Create Team" button in the teams panel (if present). */
    public Locator createTeamButton() {
        return page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Create Team"));
    }

    // ── Team name modal ───────────────────────────────────────────────────────

    public Locator teamNameInput() {
        return page.getByLabel("Team name");
    }

    public Locator teamNameModalSubmit() {
        return page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Save"));
    }

    public Locator teamNameModalCancel() {
        return page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Cancel"));
    }

    // ── Members panel (right column) ──────────────────────────────────────────

    /**
     * The "Add Users" section heading visible in the members panel once a team
     * is selected. Used as a reliable sentinel that the panel has loaded.
     */
    public Locator addMemberButton() {
        return page.getByText("Add Users");
    }

    /** A row in the members list for a given username / display name. */
    public Locator memberRow(String memberName) {
        return page.getByRole(com.microsoft.playwright.options.AriaRole.ROW)
                .filter(new Locator.FilterOptions().setHasText(memberName));
    }

    // ── Compound actions ──────────────────────────────────────────────────────

    /**
     * Open the Create Team modal, type {@code name}, and submit.
     * Waits for the modal input to disappear before returning.
     */
    public void createTeam(String name) {
        createTeamButton().click();
        teamNameInput().fill(name);
        teamNameModalSubmit().click();
        waitForHidden(teamNameInput());
    }

    /**
     * Click a team entry to select it and reveal the members panel.
     */
    public void selectTeam(String teamName) {
        teamEntry(teamName).first().click();
        waitForVisible(addMemberButton());
    }
}
