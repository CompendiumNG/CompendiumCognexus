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

import java.util.List;

import com.compendium.core.datamodel.ExternalConnection;
import com.compendium.ui.PropertyDefault;

/**
 * This interface provides an API to the core application properties which are
 * stored in the AdminDatabase.properties file.
 * <p>
 * See <code>PropertyManager</code> class which allows to retrive an object
 * which implements this interface.
 * <p>
 * Each property has two corresponded method: accessor with 'get' or 'is' prefix
 * and mutator with prefix 'set'. Accessor and motator are similar to classic
 * getter and setter for a class property.
 * <p>
 * Accessor method may have <code>@PropertyDefault</code> and
 * <code>@PropertyAlias</code> annotations. The first one allows to define
 * default value for the property if it is not in the property file.
 * <code>@PropertyAlias</code> should be used when some property
 * is renamed and its old name is added into the alias annotation.     
 */
public interface DBAdminProperties {

    /** The accessor for the admin database name.*/
    @PropertyDefault(value = "compendium")
	String getDatabaseName();

    /** The mutator for the admin database name.*/
    void setDatabaseName(String databaseName);

    /** The accessor for the list of extra MySQL connection profiles.*/
    @PropertyDefault(value = "")
    List<ExternalConnection> getExtraMsqlConnectionProfiles();
}
