package de.koware.emalemip.dalamander.shadow;

import de.koware.emalemip.dalamander.deployment.EmalemipDeployment;

/*
    Objects of this class 'shadow' a EmalemipDeployment, e.g. they track and process all events from that deployment.

    This is especially relevant for Deployment that incorporate human interaction.
 */
public class Shadow {

    private EmalemipDeployment deployment;


    public EmalemipDeployment getDeployment() {
        return this.deployment;
    }

    public void setDeployment(EmalemipDeployment deployment) {
        this.deployment = deployment;
    }
}
