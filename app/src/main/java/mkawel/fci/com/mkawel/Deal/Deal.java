package mkawel.fci.com.mkawel.Deal;

public class Deal {
    int Id;
    int userId;
    int workerId;
    String dealName;
    String expectedTime;
    float cost;
    String description;
    float rateDeal;
    String userName;
    String userImage;
    float userRate;

    public Deal() {
    }

    public Deal(int id, int userId, int workerId, String dealName, String expectedTime, float cost, String description, float rateDeal) {
        Id = id;
        this.userId = userId;
        this.workerId = workerId;
        this.dealName = dealName;
        this.expectedTime = expectedTime;
        this.cost = cost;
        this.description = description;
        this.rateDeal = rateDeal;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserRate(float userRate) {
        this.userRate = userRate;
    }

    public float getUserRate() {
        return userRate;
    }

    public String getUserImage() {
        return userImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setId(int id) {
        Id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public void setDealName(String dealName) {
        this.dealName = dealName;
    }

    public void setExpectedTime(String expectedTime) {
        this.expectedTime = expectedTime;
    }

    public void setRateDeal(float rateDeal) {
        this.rateDeal = rateDeal;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setWorkerId(int workerId) {
        this.workerId = workerId;
    }

    public String getExpectedTime() {
        return expectedTime;
    }

    public int getId() {
        return Id;
    }

    public String getDescription() {
        return description;
    }

    public float getCost() {
        return cost;
    }

    public float getRateDeal() {
        return rateDeal;
    }

    public int getUserId() {
        return userId;
    }

    public int getWorkerId() {
        return workerId;
    }

    public String getDealName() {
        return dealName;
    }
}

