import org.apache.commons.collections.map.HashedMap;

import java.util.Map;

public class FingerSample {
    Integer series;
    Integer sample;
    String side;
    Features2D features2D;

    public class Features2D {
        Double fifth;
        Double fourth;
        Double first;
        Double third;
        Double second;

        @Override
        public String toString() {
            return "Features2D{" +
                    "fifth=" + fifth +
                    ", fourth=" + fourth +
                    ", first=" + first +
                    ", third=" + third +
                    ", second=" + second +
                    '}';
        }

        public Map<String, Double> getFingerValuesMap() {
            final Map<String, Double> map = new HashedMap();
            map.put("first", first);
            map.put("second", second);
            map.put("third", third);
            map.put("fourth", fourth);
            map.put("fifth", fifth);
            return map;
        }
    }

    @Override
    public String toString() {
        return "FingerSample{" +
                "series=" + series +
                ", sample=" + sample +
                ", side='" + side + '\'' +
                ", features2D=" + features2D +
                '}';
    }
}
