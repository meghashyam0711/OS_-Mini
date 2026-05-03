# First Fit Memory Allocation Simulator

A graphical memory allocation simulator built using Java Swing. This tool demonstrates the "First Fit" contiguous memory allocation technique, a fundamental concept taught in Operating Systems courses.

## Features
- **Interactive GUI**: User-friendly interface with input fields to define custom memory partition sizes and process requirements.
- **Visual Representation**: Dynamically renders memory blocks and process allocations using color-coded graphics.
- **First Fit Algorithm**: Accurately implements the First Fit memory allocation strategy, where each process is allocated to the first available partition that is large enough to hold it.
- **Allocation Status Summary**: Clearly displays the status of processes that could not be allocated due to insufficient contiguous memory.

## Prerequisites
- Java Development Kit (JDK) 8 or higher installed on your system.

## How to Run
1. Open a terminal or command prompt and navigate to the project directory.
2. Compile the Java source file:
   ```bash
   javac MemorySimulator.java
   ```
3. Run the compiled application:
   ```bash
   java MemorySimulator
   ```

## Usage
1. Launch the application.
2. In the **Partitions** field, enter memory block sizes in KB, separated by commas or spaces (e.g., `100, 500, 200, 300, 600`).
3. In the **Processes** field, enter process memory requirements in KB, separated by commas or spaces (e.g., `212, 417, 112, 426`).
4. Click the **"Simulate Allocation"** button to visualize how the processes are mapped to the memory partitions.

## Visual Guide
- **Light Gray Block**: Base empty memory partition.
- **Green Section**: Space successfully allocated to a specific process.
- **Red Section (Waste)**: Internal fragmentation within a used partition.
- **"FREE"**: Partitions that are completely unused.

## Project Structure
- `MemorySimulator.java`: The main Java source code containing the Swing GUI setup, rendering engine (`VisualizerPanel`), and the First Fit allocation logic.
