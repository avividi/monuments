package avividi.com.monuments.controller.alert;

public class AlertManager {

  private String alertText = "";
  private final static int tick_alertDisplayTime = 10;
  private int alertDisplayCounter = tick_alertDisplayTime;

  public void every10Ticks() {
    if (alertDisplayCounter == 0) {
      alertText = "";
    }
    else alertDisplayCounter--;
  }

  public void setAlertText(String alertText) {
    this.alertText = alertText;
    this.alertDisplayCounter = tick_alertDisplayTime;
  }

  public String getAlertText() {
    return alertText;
  }
}
