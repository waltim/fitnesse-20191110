package fitnesse.plugins.slimcoverage;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class SlimScenarioUsage {
    private final Map<String, SlimScenarioUsagePer> usagePerPage = new LinkedHashMap<>();

    public SlimScenarioUsagePer getUsageByPage(String pageName) {
        if (!usagePerPage.containsKey(pageName)) {
            usagePerPage.put(pageName, new SlimScenarioUsagePer(pageName));
        }
        return usagePerPage.get(pageName);
    }

    public List<SlimScenarioUsagePer> getUsage() {
        return new ArrayList<>(usagePerPage.values());
    }

    public SlimScenarioUsagePer getScenarioUsage() {
        SlimScenarioUsagePer result = new SlimScenarioUsagePer("Total per scenario");
        usagePerPage.values().forEach((value) -> {
            value.getUsage().entrySet().forEach((entry) -> {
                result.addUsage(entry.getKey(), entry.getValue());
            });
        });
        return result;
    }

    public Collection<String> getUnusedScenarios() {
        List<String> result = new ArrayList<>();
        getScenarioUsage().getUsage().entrySet().stream().filter((usage) -> (usage.getValue() < 1)).forEachOrdered((usage) -> {
            result.add(usage.getKey());
        });
        return result;
    }

    public Collection<String> getUsedScenarios() {
        List<String> result = new ArrayList<>();
        getScenarioUsage().getUsage().entrySet().stream().filter((usage) -> (usage.getValue() > 0)).forEachOrdered((usage) -> {
            result.add(usage.getKey());
        });
        return result;
    }

    public Collection<String> getOverriddenScenarios() {
        Set<String> result = new HashSet<>();
        getOverriddenScenariosPerPage().entrySet().forEach((usage) -> {
            result.addAll(usage.getValue());
        });
        return result;
    }

    public Map<String, Collection<String>> getOverriddenScenariosPerPage() {
        Map<String, Collection<String>> result = new LinkedHashMap<>();
        usagePerPage.entrySet().stream().filter((value) -> (!value.getValue().getOverriddenScenarios().isEmpty())).forEachOrdered((value) -> {
            result.put(value.getKey(), value.getValue().getOverriddenScenarios());
        });
        return result;
    }

    public Map<String, Collection<String>> getPagesUsingScenario() {
        Map<String, Collection<String>> result = new LinkedHashMap<>();
        usagePerPage.entrySet().forEach((value) -> {
            String page = value.getKey();
            value.getValue().getUsage().entrySet().stream().filter((entry) -> (entry.getValue() > 0)).map((entry) -> entry.getKey()).map((scenario) -> getOrCreateCollection(result, scenario)).forEachOrdered((pagesUsingScenario) -> {
                pagesUsingScenario.add(page);
            });
        });
        return result;
    }

    public Map<String, Collection<String>> getScenariosBySmallestScope() {
        Map<String, Collection<String>> result = new LinkedHashMap<>();
        Map<String, Collection<String>> pagesPerScenario = getPagesUsingScenario();
        pagesPerScenario.entrySet().forEach((ppsEntry) -> {
            String scenario = ppsEntry.getKey();
            Collection<String> pages = ppsEntry.getValue();
            String scope = getLongestSharedPath(pages);
            Collection<String> scenariosForScope = getOrCreateCollection(result, scope);
            scenariosForScope.add(scenario);
        });
        return result;
    }

    private String getLongestSharedPath(Collection<String> pages) {
        String result;
        if (pages.size() == 1) {
            result = pages.iterator().next();
        } else {
            List<String> pageNames = new ArrayList<>(pages);
            String longestPrefix = StringUtils.getCommonPrefix(pageNames.toArray(new String[pageNames.size()]));
            if (longestPrefix.endsWith(".")) {
                result = longestPrefix.substring(0, longestPrefix.lastIndexOf("."));
            } else {
                if (pageNames.contains(longestPrefix)) {
                    result = longestPrefix;
                } else {
                    int lastDot = longestPrefix.lastIndexOf(".");
                    result = longestPrefix.substring(0, lastDot);
                }
            }
        }
        return result;
    }

    protected Collection<String> getOrCreateCollection(Map<String, Collection<String>> map, String scope) {
        Collection<String> value = map.get(scope);
        if (value == null) {
            value = new ArrayList<>();
            map.put(scope, value);
        }
        return value;
    }

    @Override
    public String toString() {
        return "ScenarioUsage: " + usagePerPage;
    }
}
