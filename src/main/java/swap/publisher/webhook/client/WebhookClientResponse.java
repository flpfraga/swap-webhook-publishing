package swap.publisher.webhook.client;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa a resposta enviada ao webhook com informações de repositório
 */
public class WebhookClientResponse {
    private String user;
    private String repository;
    private List<Issue> issues;
    private List<Contributor> contributors;

    public WebhookClientResponse() {
    }

    public WebhookClientResponse(String user,
                                 String repository,
                                 List<swap.producer.info.Issue> issues,
                                 List<swap.producer.info.Contributor> contributors) {
        this.user = user;
        this.repository = repository;
        this.issues = new ArrayList<>(issues.stream().map(issue -> new Issue(
                issue.getTitle().toString(), issue.getAuthor().toString(), issue.getLabels().toString()))
                .toList());
        this.contributors = new ArrayList<>(contributors.stream().map(contributor -> new Contributor(
                        contributor.getName().toString(),
                        contributor.getUser().toString(),
                        contributor.getQtdCommits()))
                .toList());
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
    }

    public List<Contributor> getContributors() {
        return contributors;
    }

    public void setContributors(List<Contributor> contributors) {
        this.contributors = contributors;
    }

    public class Issue {
        private String title;
        private String author;
        private String labels;

        public Issue() {
        }

        public Issue(String title, String author, String labels) {
            this.title = title;
            this.author = author;
            this.labels = labels;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getLabels() {
            return labels;
        }

        public void setLabels(String labels) {
            this.labels = labels;
        }
    }

    public class Contributor {
        private String name;
        private String user;
        private Integer qtd_commits;

        public Contributor() {
        }

        public Contributor(String name, String user, Integer qtd_commits) {
            this.name = name;
            this.user = user;
            this.qtd_commits = qtd_commits;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public Integer getQtd_commits() {
            return qtd_commits;
        }

        public void setQtd_commits(Integer qtd_commits) {
            this.qtd_commits = qtd_commits;
        }
    }
}
