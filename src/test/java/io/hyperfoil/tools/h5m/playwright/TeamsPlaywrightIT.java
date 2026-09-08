package io.hyperfoil.tools.h5m.playwright;

import io.hyperfoil.tools.h5m.playwright.pages.TeamsPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * End-to-end Playwright tests for the Teams page ({@code /teams}).
 *
 * <p>Each test navigates to a fresh page and exercises one user-visible
 * behaviour on the Teams management UI. Tests are independent: each gets
 * an isolated {@link com.microsoft.playwright.BrowserContext} from
 * {@link PlaywrightTestBase}.
 *
 * <p>Run with:
 * <pre>{@code
 *   ./mvnw verify -Dplaywright [-Dplaywright.baseUrl=http://localhost:8080]
 * }</pre>
 */
public class TeamsPlaywrightIT extends PlaywrightTestBase {

    private TeamsPage teamsPage;

    @BeforeEach
    void setUp() {
        teamsPage = new TeamsPage(page);
        teamsPage.open();
    }

    // ── Layout ────────────────────────────────────────────────────────────────

    @Test
    void teamsPage_showsSelectTeamPrompt_initially() {
        assertThat(teamsPage.selectTeamNotification()).isVisible();
    }

    @Test
    void teamsPage_createTeamButton_isVisible() {
        assertThat(teamsPage.createTeamButton()).isVisible();
    }

    // ── Create team ───────────────────────────────────────────────────────────

    @Test
    void createTeam_appearsInList() {
        String teamName = "pw-team-" + System.currentTimeMillis();

        teamsPage.createTeam(teamName);

        assertThat(teamsPage.teamEntry(teamName).first()).isVisible();
    }

    @Test
    void createTeam_cancelDoesNotCreate() {
        teamsPage.createTeamButton().click();
        assertThat(teamsPage.teamNameInput()).isVisible();
        teamsPage.teamNameInput().fill("should-not-appear");
        teamsPage.teamNameModalCancel().click();
        assertThat(teamsPage.teamNameInput()).not().isVisible();
    }

    // ── Select team → members panel ───────────────────────────────────────────

    @Test
    void selectTeam_showsMembersPanel() {
        String teamName = "pw-team-sel-" + System.currentTimeMillis();
        teamsPage.createTeam(teamName);

        teamsPage.selectTeam(teamName);

        assertThat(teamsPage.addMemberButton()).isVisible();
    }

    @Test
    void selectTeam_hidesSelectTeamPrompt() {
        String teamName = "pw-team-hide-" + System.currentTimeMillis();
        teamsPage.createTeam(teamName);

        teamsPage.selectTeam(teamName);

        assertThat(teamsPage.selectTeamNotification()).not().isVisible();
    }
}
