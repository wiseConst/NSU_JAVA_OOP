package MessageHistory;

import java.util.ArrayList;

public class MessageHistory {

    public class Entry {
        private String username;
        private String message;

        public Entry(String username, String message) {
            this.username = username;
            this.message = message;
        }

        public final String getMessage() {
            return message;
        }

        public final String getUsername() {
            return username;
        }
    }

    private Integer currentUpdateMessageIndex = 0;
    private Integer messagesCount = 0;
    private ArrayList<Entry> history = null;

    public MessageHistory(Integer messagesCount) {
        this.messagesCount = messagesCount;
        history = new ArrayList<>(messagesCount);
    }

    public synchronized void update(String username, String message) {
        if (history != null && messagesCount > 0) {
            if (history.size() <= messagesCount) {
                history.add(new Entry(username, message));
            } else {
                history.set(currentUpdateMessageIndex, new Entry(username, message));
                currentUpdateMessageIndex = (currentUpdateMessageIndex + 1) % messagesCount;
            }
        }
    }

    public synchronized final ArrayList<Entry> getHistory() {
        return history;
    }

}
