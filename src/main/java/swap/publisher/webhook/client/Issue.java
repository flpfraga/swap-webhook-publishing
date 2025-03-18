package swap.publisher.webhook.client;

/**
 * Representa uma issue de um repositório GitHub
 */
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