package fitnesse.plugins.slimcoverage;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import fitnesse.slim.instructions.Instruction;
import fitnesse.testrunner.WikiTestPage;
import fitnesse.testsystems.TestExecutionException;
import fitnesse.testsystems.TestPage;
import fitnesse.testsystems.TestSummary;
import fitnesse.testsystems.UnableToStopException;
import fitnesse.testsystems.slim.CustomComparatorRegistry;
import fitnesse.testsystems.slim.HtmlSlimTestSystem;
import fitnesse.testsystems.slim.SlimClient;
import fitnesse.testsystems.slim.SlimTestContextImpl;
import fitnesse.testsystems.slim.tables.SlimTable;
import fitnesse.testsystems.slim.tables.SlimTableFactory;
import fitnesse.wiki.WikiPageDummy;

public class SlimCoverageTestSystem extends HtmlSlimTestSystem {
    private final SlimScenarioUsage usage;

    public SlimCoverageTestSystem(String testSystemName, SlimTableFactory slimTableFactory, CustomComparatorRegistry customComparatorRegistry) {
        super(testSystemName, dummySlimClient(), slimTableFactory, customComparatorRegistry);
        this.usage = new SlimScenarioUsage();
    }

    private static SlimClient dummySlimClient() {
        return new SlimClient() {
            @Override
            public void start() {
            }

            @Override
            public Map<String, Object> invokeAndGetResponse(List<Instruction> statements) {
                return null;
            }

            @Override
            public void connect() {
            }

            @Override
            public void bye() {
            }

            @Override
            public void kill() {
            }
        };
    }

    public SlimScenarioUsage getUsage() {
        return usage;
    }

    @Override
    protected SlimTestContextImpl createTestContext(TestPage testPage) {
        String fullPath = testPage.getFullPath();
        SlimScenarioUsagePer usageByPage = usage.getUsageByPage(fullPath);
        return new SlimCoverageTestContextImpl(testPage, usageByPage);
    }

    @Override
    protected void processTable(SlimTable table, boolean isSuiteTearDownPage) throws TestExecutionException {
        table.getAssertions();
    }

    @Override
    protected void testStarted(TestPage testPage) {
        super.testStarted(testPage);
        // ensure we have a single test passed, which is sometimes a requirement
        // (i.e. when run by FitNesseRunner)
        getTestContext().incrementPassedTestsCount();
    }

    @Override
    public void bye() throws UnableToStopException {
        try {
            reportScenarioUsage();
        } finally {
            super.bye();
        }
    }

    protected void reportScenarioUsageHeader(String header) {
        testOutputChunk("<h4>" + header + "</h4>");
    }

    protected void reportScenarioUsageNewline() {
        testOutputChunk("<br/>");
    }

    protected void reportScenarioUsage() {
        WikiPageDummy pageDummy = new WikiPageDummy("Scenario Usage Report", "Scenario Usage Report Content", null);
        WikiTestPage testPage = new WikiTestPage(pageDummy);
        testStarted(testPage);

        Map<String, Integer> totalUsage = usage.getScenarioUsage().getUsage();
        if (totalUsage.isEmpty()) {
            testOutputChunk("No scenarios in run");
        } else {
            Collection<String> unused = usage.getUnusedScenarios();
            if (!unused.isEmpty()) {
                reportScenarioUsageHeader("Unused scenarios:");
                testOutputChunk("<ul>");
                unused.forEach((scenarioName) -> {
                    testOutputChunk("<li>" + scenarioName + "</li>");
                });
                testOutputChunk("</ul>");
                reportScenarioUsageNewline();
            }

            reportScenarioUsageHeader("Total usage count per scenario:");
            testOutputChunk("<table>");
            testOutputChunk("<tr><th>Scenario</th><th>Count</th></tr>");
            totalUsage.entrySet().stream().map((totalUsageEntry) -> {
                testOutputChunk("<tr>");
                return totalUsageEntry;
            }).map((totalUsageEntry) -> {
                testOutputChunk("<td>");
                return totalUsageEntry;
            }).map((totalUsageEntry) -> {
                testOutputChunk(totalUsageEntry.getKey()
                        + "</td><td>"
                        + totalUsageEntry.getValue());
                return totalUsageEntry;
            }).map((_item) -> {
                testOutputChunk("</td>");
                return _item;
            }).forEachOrdered((_item) -> {
                testOutputChunk("</tr>");
            });
            testOutputChunk("</table>");
            reportScenarioUsageNewline();

            reportScenarioUsageHeader("Scenarios grouped by usage scope:");
            testOutputChunk("<ul>");
            usage.getScenariosBySmallestScope().entrySet().stream().map((sByScopeEntry) -> {
                String scope = sByScopeEntry.getKey();
                testOutputChunk("<li>");
                testOutputChunk(scope);
                testOutputChunk("<ul>");
                return sByScopeEntry;
            }).map((sByScopeEntry) -> {
                sByScopeEntry.getValue().forEach((scenario) -> {
                    testOutputChunk("<li>" + scenario + "</li>");
                });
                return sByScopeEntry;
            }).map((_item) -> {
                testOutputChunk("</ul>");
                return _item;
            }).forEachOrdered((_item) -> {
                testOutputChunk("</li>");
            });
            testOutputChunk("</ul>");
            reportScenarioUsageNewline();

            reportScenarioUsageHeader("Usage count per scenario per page:");
            testOutputChunk("<table>");
            testOutputChunk("<tr><th>Page</th><th>Scenario</th><th>Count</th></tr>");
            usage.getUsage().forEach((usagePerPage) -> {
                String pageName = usagePerPage.getGroupName();
                usagePerPage.getUsage().entrySet().stream().map((usagePerScenario) -> {
                    testOutputChunk("<tr>");
                    return usagePerScenario;
                }).map((usagePerScenario) -> {
                    testOutputChunk("<td>");
                    return usagePerScenario;
                }).map((usagePerScenario) -> {
                    testOutputChunk(pageName
                            + "</td><td>"
                            + usagePerScenario.getKey()
                            + "</td><td>"
                            + usagePerScenario.getValue());
                    return usagePerScenario;
                }).map((_item) -> {
                    testOutputChunk("</td>");
                    return _item;
                }).forEachOrdered((_item) -> {
                    testOutputChunk("</tr>");
                });
            });
            testOutputChunk("</table>");

            Map<String, Collection<String>> overriddenPerPage = usage.getOverriddenScenariosPerPage();
            if (!overriddenPerPage.isEmpty()) {
                reportScenarioUsageNewline();
                reportScenarioUsageHeader("Overridden scenario(s) per page:");
                testOutputChunk("<ul>");
                overriddenPerPage.entrySet().stream().map((overriddenForPage) -> {
                    String pageName = overriddenForPage.getKey();
                    testOutputChunk("<li>");
                    testOutputChunk(pageName);
                    testOutputChunk("<ul>");
                    return overriddenForPage;
                }).map((overriddenForPage) -> {
                    overriddenForPage.getValue().forEach((scenario) -> {
                        testOutputChunk("<li>" + scenario + "</li>");
                    });
                    return overriddenForPage;
                }).map((_item) -> {
                    testOutputChunk("</ul>");
                    return _item;
                }).forEachOrdered((_item) -> {
                    testOutputChunk("</li>");
                });
                testOutputChunk("</ul>");
            }
        }
        testComplete(testPage, new TestSummary(0, 0, 1, 0));
    }

}
