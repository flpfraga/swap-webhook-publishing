package swap.publisher.webhook.client;

/**
 * Representa um contribuidor de um repositório GitHub
 */
public class Contributor {
    private String name;
    private String user;
    private Integer qtdCommits;

    public Contributor() {
    }

    public Contributor(String name, String user, Integer qtdCommits) {
        this.name = name;
        this.user = user;
        this.qtdCommits = qtdCommits;
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

    public Integer getQtdCommits() {
        return qtdCommits;
    }

    public void setQtdCommits(Integer qtdCommits) {
        this.qtdCommits = qtdCommits;
    }
} 