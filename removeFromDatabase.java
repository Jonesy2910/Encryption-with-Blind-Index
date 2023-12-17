package org.apache.maven.archetypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class removeFromDatabase {

    public void removeRow(int rowIndex) {
        try (Connection conn = DBConnection.getConnection()) {
            //Selecting rows where ID equals checked box then deleting row
            String deleteQuery = "DELETE FROM information WHERE id = ?";
            PreparedStatement deleteStatement = conn.prepareStatement(deleteQuery);
            deleteStatement.setInt(1, rowIndex);
            deleteStatement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
