package ru.azat.vaadin.crud.common;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;

public abstract class BasicView extends AppLayout {

    private VerticalLayout drawerLayout;

    public BasicView() {
        init(false);
    }

    public BasicView(boolean drawerOpened) {
        init(drawerOpened);
    }

    private void init(boolean drawerOpened) {
        this.drawerLayout = new VerticalLayout();
        this.setDrawerOpened(drawerOpened);
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("Vaadin");
        logo.setHeight("40px");
        logo.getStyle().set("margin","0px");
        logo.getStyle().set("padding-bottom","20px");
//
//        Anchor logout = new Anchor("/logout", "Log out");
//        logout.getStyle().set("padding-right","20px");

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(),logo);
        header.addClassName("header");
        header.setWidth("100%");
        header.expand(logo);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        addToNavbar(header);
    }

    private void createDrawer() {
        addToDrawer(drawerLayout);
    }

    public void addRouterLinkToDrawer(RouterLink... routerLinks) {
        drawerLayout.add(routerLinks);
    }

    public void setContent(Component component) {
        super.setContent(component);
    }

}
