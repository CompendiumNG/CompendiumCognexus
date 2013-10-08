/********************************************************************************
 *                                                                              *
 *  (c) Copyright 2009 Verizon Communications USA and The Open University UK    *
 *                                                                              *
 *  This software is freely distributed in accordance with                      *
 *  the GNU Lesser General Public (LGPL) license, version 3 or later            *
 *  as published by the Free Software Foundation.                               *
 *  For details see LGPL: http://www.fsf.org/licensing/licenses/lgpl.html       *
 *               and GPL: http://www.fsf.org/licensing/licenses/gpl-3.0.html    *
 *                                                                              *
 *  This software is provided by the copyright holders and contributors "as is" *
 *  and any express or implied warranties, including, but not limited to, the   *
 *  implied warranties of merchantability and fitness for a particular purpose  *
 *  are disclaimed. In no event shall the copyright owner or contributors be    *
 *  liable for any direct, indirect, incidental, special, exemplary, or         *
 *  consequential damages (including, but not limited to, procurement of        *
 *  substitute goods or services; loss of use, data, or profits; or business    *
 *  interruption) however caused and on any theory of liability, whether in     *
 *  contract, strict liability, or tort (including negligence or otherwise)     *
 *  arising in any way out of the use of this software, even if advised of the  *
 *  possibility of such damage.                                                 *
 *                                                                              *
 ********************************************************************************/
package com.compendium.core.db.management;

import com.compendium.ui.PropertyManager;

/**
 * This is a DBAdminProperties provider. The properties are read from
 * System/resources/DbAdmin.properties file. There are a few versions of this
 * file (see etc/configuration directory) which are used in the corresponded
 * configurations of Compendium.
 */
public class DBAdminPropertiesProvider {
    private static final String DB_ADMIN_PROPERTIES = "DbAdmin.properties";
    private static final PropertyManager<DBAdminProperties> MANAGER =
        new PropertyManager<DBAdminProperties>(
                DBAdminProperties.class, DB_ADMIN_PROPERTIES);
    
    /**
     * Returns DBAdminProperties object which provides an interface to  
     * DbAdmin.properties file.
     * 
     * @return DBAdminProperties instance for DBAdminProperties. 
     */
    public DBAdminProperties get() {
        return MANAGER.load(); 
    }
}
