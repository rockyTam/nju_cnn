import java.util.List;

/**
 * Created by Rocky on 2017/12/25.
 */
public class LawList {
    String fullTextId;
    List<LawForMongo> references;

    public String getFullTextId() {
        return fullTextId;
    }

    public void setFullTextId(String fullTextId) {
        this.fullTextId = fullTextId;
    }

    public List<LawForMongo> getReferences() {
        return references;
    }

    public void setReferences(List<LawForMongo> references) {
        this.references = references;
    }

    Oid _id;
    public class Oid{
        String $oid;
        public String get$oid() {
            return $oid;
        }

        public void set$oid(String $oid) {
            this.$oid = $oid;
        }

    }
}
