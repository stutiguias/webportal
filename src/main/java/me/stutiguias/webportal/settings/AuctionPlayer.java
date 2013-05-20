package me.stutiguias.webportal.settings;

public class AuctionPlayer {

    private int id;
    private String name;
    private String pass;
    private double money;
    private int canBuy;
    private int canSell;
    private int isAdmin;
    private boolean isLogin;
    private String ip;
    private String webban;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public int getCanBuy() {
        return canBuy;
    }

    public void setCanBuy(int canBuy) {
        this.canBuy = canBuy;
    }

    public int getCanSell() {
        return canSell;
    }

    public void setCanSell(int canSell) {
        this.canSell = canSell;
    }

    public int getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(int isAdmin) {
        this.isAdmin = isAdmin;
    }

    public boolean isLogin() {
        return isLogin;
    }

    /**
     * @return the webban
     */
    public String getWebban() {
        return webban;
    }

    /**
     * @param webban the webban to set
     */
    public void setWebban(String webban) {
        this.webban = webban;
    }
}
