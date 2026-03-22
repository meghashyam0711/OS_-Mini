import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class MemorySimulator extends JFrame {
    private JTextField partitionField;
    private JTextField processField;
    private VisualizerPanel visualizerPanel;

    private int[] partitions;
    private int[] processes;
    private int[] allocation;
    private int[] frag;

    public MemorySimulator() {
        // Set a clean UI Look and Feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {}

        setTitle("First Fit Memory Allocation Simulator");
        setSize(900, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // --- Top Input Panel ---
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        inputPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        inputPanel.setBackground(new Color(245, 247, 250)); // Light Gray-Blue

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Segoe UI", Font.BOLD, 15);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 15);

        JLabel partLabel = new JLabel("Partitions (KB, spaces or commas):");
        partLabel.setFont(labelFont);
        partLabel.setForeground(new Color(44, 62, 80));
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(partLabel, gbc);

        partitionField = new JTextField("100, 500, 200, 300, 600", 25);
        partitionField.setFont(fieldFont);
        gbc.gridx = 1; gbc.gridy = 0;
        inputPanel.add(partitionField, gbc);

        JLabel procLabel = new JLabel("Processes (KB, spaces or commas):");
        procLabel.setFont(labelFont);
        procLabel.setForeground(new Color(44, 62, 80));
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(procLabel, gbc);

        processField = new JTextField("212, 417, 112, 426", 25);
        processField.setFont(fieldFont);
        gbc.gridx = 1; gbc.gridy = 1;
        inputPanel.add(processField, gbc);

        JButton simulateBtn = new JButton("Simulate Allocation");
        simulateBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        simulateBtn.setBackground(new Color(52, 152, 219)); // Nice Blue
        simulateBtn.setForeground(Color.WHITE);
        simulateBtn.setFocusPainted(false);
        simulateBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        inputPanel.add(simulateBtn, gbc);

        add(inputPanel, BorderLayout.NORTH);

        // --- Center Visualization Panel ---
        visualizerPanel = new VisualizerPanel();
        JScrollPane scrollPane = new JScrollPane(visualizerPanel);
        scrollPane.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(220, 225, 230)));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Action listener for the button
        simulateBtn.addActionListener(e -> runSimulation());

        // Run an initial simulation so it's not empty
        runSimulation();
    }

    private void runSimulation() {
        try {
            // Parse partitions
            String[] partStrs = partitionField.getText().trim().split("[,\\s]+");
            partitions = new int[partStrs.length];
            for (int i = 0; i < partStrs.length; i++) {
                partitions[i] = Integer.parseInt(partStrs[i].trim());
            }

            // Parse processes
            String[] procStrs = processField.getText().trim().split("[,\\s]+");
            processes = new int[procStrs.length];
            allocation = new int[procStrs.length];
            for (int i = 0; i < procStrs.length; i++) {
                processes[i] = Integer.parseInt(procStrs[i].trim());
                allocation[i] = -1; // Unallocated initially
            }

            // First Fit Logic -> Supporting Multiple Processes per Block
            int[] tempParts = partitions.clone();
            for (int i = 0; i < processes.length; i++) {
                for (int j = 0; j < tempParts.length; j++) {
                    if (tempParts[j] >= processes[i]) {
                        allocation[i] = j; 
                        tempParts[j] -= processes[i]; // Subtract space instead of marking fully occuppied
                        break;
                    }
                }
            }

            // Leftover memory forms the fragmentation
            frag = tempParts; 

            // Update UI
            visualizerPanel.updateData(partitions, processes, allocation, frag);
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Invalid input! Please enter only numbers separated by spaces or commas.", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Custom Drawing Panel for Memory Blocks ---
    class VisualizerPanel extends JPanel {
        private int[] parts;
        private int[] procs;
        private int[] allocs;
        private int[] frags;

        public VisualizerPanel() {
            setBackground(Color.WHITE);
        }

        public void updateData(int[] partitions, int[] processes, int[] allocation, int[] frag) {
            this.parts = partitions;
            this.procs = processes;
            this.allocs = allocation;
            this.frags = frag;
            
            int requiredHeight = 150 + (parts.length * 120);
            setPreferredSize(new Dimension(850, Math.max(500, requiredHeight)));
            revalidate();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (parts == null) return;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Title
            g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
            g2.setColor(new Color(44, 62, 80));
            String title = "Memory Allocation View";
            int titleWidth = g2.getFontMetrics().stringWidth(title);
            g2.drawString(title, (getWidth() - titleWidth) / 2, 40);

            int startX = Math.max(50, (getWidth() - 600) / 2);
            int blockWidth = 600;
            int blockHeight = 70;

            for (int j = 0; j < parts.length; j++) {
                int yPos = 90 + (j * 120);

                // Draw Block Label
                g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
                g2.setColor(new Color(127, 140, 141));
                g2.drawString("Partition " + j + " (" + parts[j] + " KB)", startX, yPos - 10);

                // Draw Base Empty Block (Light Gray)
                g2.setColor(new Color(236, 240, 241));
                g2.fillRoundRect(startX, yPos, blockWidth, blockHeight, 15, 15);

                boolean isUsed = false;
                for (int i = 0; i < procs.length; i++) {
                    if (allocs[i] == j) {
                        isUsed = true;
                        break;
                    }
                }

                if (isUsed) {
                    Shape oldClip = g2.getClip();
                    g2.clip(new RoundRectangle2D.Float(startX, yPos, blockWidth, blockHeight, 15, 15));
                    
                    int currentX = startX;
                    int colorToggle = 0;

                    for (int i = 0; i < procs.length; i++) {
                        if (allocs[i] == j) {
                            double ratio = (double) procs[i] / parts[j];
                            int procW = (int) (ratio * blockWidth);

                            // Draw Allocated Process Space
                            if (colorToggle % 2 == 0) {
                                g2.setColor(new Color(46, 204, 113));
                                g2.fillRect(currentX, yPos, procW, blockHeight);
                                g2.setColor(new Color(39, 174, 96));
                            } else {
                                g2.setColor(new Color(39, 174, 96));
                                g2.fillRect(currentX, yPos, procW, blockHeight);
                                g2.setColor(new Color(24, 138, 70));
                            }
                            g2.drawRect(currentX, yPos, procW, blockHeight);

                            // Process Text
                            g2.setColor(Color.WHITE);
                            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                            String pStr = "P" + (i + 1) + " (" + procs[i] + "K)";
                            int pStrW = g2.getFontMetrics().stringWidth(pStr);
                            if (procW > pStrW + 4) {
                                g2.drawString(pStr, currentX + (procW - pStrW) / 2, yPos + 40);
                            }
                            currentX += procW;
                            colorToggle++;
                        }
                    }

                    // Wasted Space (Red)
                    if (frags[j] > 0) {
                        int fragW = startX + blockWidth - currentX;
                        g2.setColor(new Color(231, 76, 60));
                        g2.fillRect(currentX, yPos, fragW, blockHeight);
                        
                        // Waste Text
                        g2.setColor(Color.WHITE);
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                        String wStr = frags[j] + "K Waste";
                        int wStrW = g2.getFontMetrics().stringWidth(wStr);
                        if (fragW > wStrW + 4) {
                            g2.drawString(wStr, currentX + (fragW - wStrW) / 2, yPos + 40);
                        }
                    }

                    g2.setClip(oldClip); // Remove inner clip
                } else {
                    // Block is completely free
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
                    g2.setColor(new Color(149, 165, 166));
                    String freeStr = "FREE / " + parts[j] + " KB";
                    int fStrW = g2.getFontMetrics().stringWidth(freeStr);
                    g2.drawString(freeStr, startX + (blockWidth - fStrW) / 2, yPos + 42);
                }

                // Smooth thick border encapsulating the complete chunk
                g2.setColor(new Color(189, 195, 199)); 
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(startX, yPos, blockWidth, blockHeight, 15, 15);
            }

            // Draw Process Status (What couldn't be allocated)
            drawUnallocatedSummary(g2, startX, 90 + (parts.length * 120));
        }

        private void drawUnallocatedSummary(Graphics2D g2, int x, int y) {
            y += 20;
            g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g2.setColor(new Color(192, 57, 43));
            
            boolean hasUnallocated = false;
            StringBuilder sb = new StringBuilder("Unallocated Processes: ");
            for (int i = 0; i < procs.length; i++) {
                if (allocs[i] == -1) {
                    hasUnallocated = true;
                    sb.append("P").append(i + 1).append(" (").append(procs[i]).append(" KB), ");
                }
            }

            if (hasUnallocated) {
                String msg = sb.substring(0, sb.length() - 2);
                g2.drawString(msg, x, y);
            } else {
                g2.setColor(new Color(39, 174, 96));
                g2.drawString("All processes successfully allocated if possible!", x, y);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MemorySimulator().setVisible(true);
        });
    }
}