package it.mulders.mcs.cli;

import it.mulders.mcs.search.SearchCommandHandler;
import it.mulders.mcs.search.SearchQuery;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "mcs",
        subcommands = { Cli.SearchCommand.class, Cli.ClassSearchCommand.class },
        usageHelpAutoWidth = true,
        versionProvider = ClasspathVersionProvider.class
)
public class Cli {
    private final SearchCommandHandler searchCommandHandler;

    @CommandLine.Option(
            names = { "-V", "--version" },
            description = "Show version number",
            versionHelp = true
    )
    private boolean showVersion;

    @CommandLine.Option(
            names = { "-h", "--help" },
            description = "Display this help message and exits",
            scope = CommandLine.ScopeType.INHERIT,
            usageHelp = true
    )
    boolean usageHelpRequested;

    public Cli(final SearchCommandHandler searchCommandHandler) {
        this.searchCommandHandler = searchCommandHandler;
    }

    public SearchCommand createSearchCommand() {
        return new SearchCommand();
    }

    public ClassSearchCommand createClassSearchCommand() {
        return new ClassSearchCommand();
    }

    @CommandLine.Command(
            name = "search",
            description = "Search artifacts in Maven Central by coordinates",
            usageHelpAutoWidth = true
    )
    public class SearchCommand implements Callable<Integer> {
        @CommandLine.Parameters(
                arity = "1",
                description = {
                        "What to search for.",
                        "If the search term contains a colon ( : ), it is considered a literal groupId and artifactId",
                        "Otherwise, the search term is considered a wildcard search"
                }
        )
        private String query;

        @CommandLine.Option(
                names = { "-l", "--last" },
                description = "Show <count> last versions",
                paramLabel = "<count>"
        )
        private Integer lastVersions;

        @Override
        public Integer call() {
            System.out.printf("Searching for %s...%n", query);
            var searchQuery = SearchQuery.search(this.query)
                    .withLimit(this.lastVersions)
                    .build();
            searchCommandHandler.search(searchQuery);
            return 0;
        }
    }

    @CommandLine.Command(
            name = "class-search",
            description = "Search artifacts in Maven Central by class name",
            usageHelpAutoWidth = true
    )
    public class ClassSearchCommand implements Callable<Integer> {
        @CommandLine.Parameters(
                arity = "1",
                description = {
                        "The class name to search for.",
                }
        )
        private String query;

        @CommandLine.Option(
                names = { "-f", "--full-name" },
                negatable = true,
                arity = "0",
                description = "Class name includes package"
        )
        private boolean fullName;

        @CommandLine.Option(
                names = { "-l", "--last" },
                description = "Show <count> last versions",
                paramLabel = "<count>"
        )
        private Integer lastVersions;

        @Override
        public Integer call() {
            System.out.printf("Searching for artifacts containing %s...%n", query);
            var searchQuery = SearchQuery.classSearch(this.query)
                    .isFullyQualified(this.fullName)
                    .withLimit(lastVersions)
                    .build();
            searchCommandHandler.search(searchQuery);
            return 0;
        }
    }
}
