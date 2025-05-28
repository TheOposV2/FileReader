import java.util.HashSet;
import java.util.Set;

class WordInfo{
    int count;
    Set<Integer> lines = new HashSet<>();

    @Override
    public String toString() {
        return " " +
                " " + count +
                " " + lines ;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setLines(Integer line) {
        lines.add(line);
    }

    public int getCount() {
        return count;
    }

    public Set<Integer> getLines() {
        return lines;
    }
}
