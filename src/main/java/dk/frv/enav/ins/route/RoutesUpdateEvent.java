/*
 * Copyright 2011 Danish Maritime Safety Administration. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 *   2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY Danish Maritime Safety Administration ``AS IS'' 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of Danish Maritime Safety Administration.
 * 
 */
package dk.frv.enav.ins.route;

/**
 * Event types ROUTE_ACTIVATED: A new route has been activated
 * ROUTE_DEACTIVATED: The current active route has been deactivated
 * ACTIVE_ROUTE_UPDATE: There is an update to the current active route
 */
public enum RoutesUpdateEvent {
	ROUTE_ACTIVATED, ROUTE_DEACTIVATED, ACTIVE_ROUTE_UPDATE, ROUTE_CHANGED, ROUTE_ADDED, ROUTE_REMOVED, ROUTE_VISIBILITY_CHANGED, ACTIVE_ROUTE_FINISHED, METOC_SETTINGS_CHANGED, SUGGESTED_ROUTES_CHANGED, ROUTE_METOC_CHANGED, ROUTE_WAYPOINT_DELETED, ROUTE_WAYPOINT_APPENDED, ROUTE_WAYPOINT_MOVED, ROUTE_MSI_UPDATE
};
