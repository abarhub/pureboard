package org.pureboard.pureboard.dashboard;

import lombok.Data;
import org.pureboard.pureboard.properties.DashboardProperties;

import java.util.List;

@Data
public class Dashboard {

    private String id;
    private String nom;
    private TypeDashboard type;
    private List<Card> cards;
    private DashboardProperties dashboardProperties;
}
