import javax.swing.*;
import java.awt.*;

public class MusicPlayer extends JFrame {
    private Song head, tail, current;
    private DefaultListModel<String> playlistModel;
    private JList<String> playlistDisplay;
    private JLabel nowPlayingLabel;

    private class Song {
        String title;
        String artist;
        int duration;
        Song next, prev;

        Song(String title, String artist, int duration) {
            this.title = title;
            this.artist = artist;
            this.duration = duration;
            this.next = null;
            this.prev = null;
        }
    }

    public MusicPlayer() {
        setTitle("MUSIC PLAYER USING LINKED LIST");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(30, 30, 30));

        playlistModel = new DefaultListModel<>();
        playlistDisplay = new JList<>(playlistModel);
        playlistDisplay.setBackground(new Color(40, 40, 40));
        playlistDisplay.setForeground(Color.WHITE);
        playlistDisplay.setSelectionBackground(new Color(60, 60, 60));
        playlistDisplay.setFont(new Font("Times New Roman", Font.BOLD, 18));

        nowPlayingLabel = new JLabel("NOW PLAYING: ~NONE~");
        nowPlayingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nowPlayingLabel.setForeground(new Color(0, 200, 150));
        nowPlayingLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
        nowPlayingLabel.setOpaque(true);
        nowPlayingLabel.setBackground(new Color(50, 50, 50));
        nowPlayingLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel playlistPanel = new JPanel(new BorderLayout());
        playlistPanel.add(new JScrollPane(playlistDisplay), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setBackground(new Color(30, 30, 30));

        JButton addButton = createColoredButton("ADD SONG", new Color(70, 130, 180), Color.WHITE);
        JButton removeButton = createColoredButton("DELETE SONG", new Color(178, 34, 34), Color.WHITE);
        JButton nextButton = createColoredButton("NEXT SONG", new Color(50, 205, 50), Color.WHITE);
        JButton prevButton = createColoredButton("PREVIOUS SONG", new Color(255, 165, 0), Color.WHITE);
        JButton moveUpButton = createColoredButton("SHIFT UP", new Color(65, 105, 225), Color.WHITE);
        JButton moveDownButton = createColoredButton("SHIFT DOWN", new Color(210, 105, 30), Color.WHITE);

        addButton.addActionListener(e -> addSong());
        removeButton.addActionListener(e -> removeSong());
        nextButton.addActionListener(e -> {
            playNext();
            updateNowPlaying();
        });
        prevButton.addActionListener(e -> {
            playPrevious();
            updateNowPlaying();
        });
        moveUpButton.addActionListener(e -> moveSongUp());
        moveDownButton.addActionListener(e -> moveSongDown());

        controlPanel.add(addButton);
        controlPanel.add(removeButton);
        controlPanel.add(nextButton);
        controlPanel.add(prevButton);
        controlPanel.add(moveUpButton);
        controlPanel.add(moveDownButton);

        add(nowPlayingLabel, BorderLayout.NORTH);
        add(playlistPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
    }

    private JButton createColoredButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setFont(new Font("Times New Roman", Font.BOLD, 14));
        return button;
    }

    private void addSong() {
        String title = JOptionPane.showInputDialog("ENTER SONG TITLE:");
        String artist = JOptionPane.showInputDialog("ARTIST NAME:");
        int duration = Integer.parseInt(JOptionPane.showInputDialog("SONG DURATION (seconds)"));

        Song newSong = new Song(title, artist, duration);
        if (head == null) {
            head = tail = current = newSong;
        } else {
            tail.next = newSong;
            newSong.prev = tail;
            tail = newSong;
        }

        String formattedDuration = formatDuration(duration);
        playlistModel.addElement(title + " : " + artist + " [" + formattedDuration + "]");
        updateNowPlaying();
    }

    private String formatDuration(int durationInSeconds) {
        int minutes = durationInSeconds / 60;
        int seconds = durationInSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void removeSong() {
        String title = JOptionPane.showInputDialog("Enter Song Title to Remove:");
        if (title == null || title.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Invalid input.");
            return;
        }

        title = title.toLowerCase();
        if (head == null) {
            JOptionPane.showMessageDialog(this, "Playlist is empty.");
            return;
        }

        Song temp = head;
        while (temp != null && !temp.title.toLowerCase().equals(title)) {
            temp = temp.next;
        }

        if (temp == null) {
            JOptionPane.showMessageDialog(this, "Song not found.");
            return;
        }

        if (temp == head) {
            head = head.next;
            if (head != null)
                head.prev = null;
        } else if (temp == tail) {
            tail = tail.prev;
            if (tail != null)
                tail.next = null;
        } else {
            temp.prev.next = temp.next;
            temp.next.prev = temp.prev;
        }

        String formattedDuration = formatDuration(temp.duration);
        String entry = temp.title + " : " + temp.artist + " [" + formattedDuration + "]";
        playlistModel.removeElement(entry);

        if (current == temp) current = head;
        updateNowPlaying();
    }

    private void playNext() {
        if (current == null || current.next == null) {
            JOptionPane.showMessageDialog(this, "Reached end of playlist or playlist is empty.");
            return;
        }
        current = current.next;
    }

    private void playPrevious() {
        if (current == null || current.prev == null) {
            JOptionPane.showMessageDialog(this, "Reached start of playlist or playlist is empty.");
            return;
        }
        current = current.prev;
    }

    private void moveSongUp() {
        int selectedIndex = playlistDisplay.getSelectedIndex();
        if (selectedIndex > 0) {
            Song currentSong = getSongAt(selectedIndex);
            Song previousSong = currentSong.prev;

            if (previousSong != null) {
                if (previousSong.prev != null) previousSong.prev.next = currentSong;
                if (currentSong.next != null) currentSong.next.prev = previousSong;

                currentSong.prev = previousSong.prev;
                previousSong.next = currentSong.next;
                currentSong.next = previousSong;
                previousSong.prev = currentSong;

                if (head == previousSong) head = currentSong;
                if (tail == currentSong) tail = previousSong;

                String currentEntry = playlistModel.get(selectedIndex);
                String previousEntry = playlistModel.get(selectedIndex - 1);

                playlistModel.set(selectedIndex - 1, currentEntry);
                playlistModel.set(selectedIndex, previousEntry);
                playlistDisplay.setSelectedIndex(selectedIndex - 1);
            }
        }
    }

    private void moveSongDown() {
        int selectedIndex = playlistDisplay.getSelectedIndex();
        if (selectedIndex < playlistModel.getSize() - 1) {
            Song currentSong = getSongAt(selectedIndex);
            Song nextSong = currentSong.next;

            if (nextSong != null) {
                if (currentSong.prev != null) currentSong.prev.next = nextSong;
                if (nextSong.next != null) nextSong.next.prev = currentSong;

                nextSong.prev = currentSong.prev;
                currentSong.next = nextSong.next;
                currentSong.prev = nextSong;
                nextSong.next = currentSong;

                if (head == currentSong) head = nextSong;
                if (tail == nextSong) tail = currentSong;

                String currentEntry = playlistModel.get(selectedIndex);
                String nextEntry = playlistModel.get(selectedIndex + 1);

                playlistModel.set(selectedIndex, nextEntry);
                playlistModel.set(selectedIndex + 1, currentEntry);
                playlistDisplay.setSelectedIndex(selectedIndex + 1);
            }
        }
    }



    private Song getSongAt(int index) {
        Song temp = head;
        for (int i = 0; i < index && temp != null; i++) {
            temp = temp.next;
        }
        return temp;
    }


    private void updateNowPlaying() {
        if (current == null) {
            nowPlayingLabel.setText("Now Playing: None");
        } else {
            nowPlayingLabel.setText("Now Playing: " + current.title + " by " + current.artist);
        }
    }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MusicPlayer().setVisible(true));
    }
}
