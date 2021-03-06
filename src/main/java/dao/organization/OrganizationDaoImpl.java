package dao.organization;

import dao.util.ConnectorToRepo;
import domain.Organization;
import org.apache.log4j.Logger;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import java.sql.*;
import java.util.UUID;

@Stateless
@Local(OrganizationDao.class)
public class OrganizationDaoImpl implements OrganizationDao {

    private static final Logger logger = Logger.getLogger(OrganizationDaoImpl.class);

    private final String GET_QUERY = "SELECT id, name, physicalAddress, legalAddress " + //TODO add manager
            "FROM Organization LIMIT 1";

    private final String PUT_QUERY = "INSERT INTO Organization (id, name, physicalAddress, legalAddress) VALUES (?, ?, ?, ?)";

    private final String DELETE_QUERY = "DELETE FROM Organization WHERE id= ?";

    private final String POST_QUERY = "UPDATE Organization SET name=?, physicalAddress=?, legalAddress=? WHERE id=?";

    private final String [] COLUMN_NAMES = {"id", "name", "physicaladdress", "legaladdress"};

    @EJB
    ConnectorToRepo connectorToRepo;


    @Override
    public Organization get() {
        logger.info("start");
        Organization organization = new Organization();
        logger.info("Organization fullfilling started >>");

        try (Connection connection = connectorToRepo.getConnection();
             Statement statement = connection.createStatement()){

            ResultSet resultSet = statement.executeQuery(GET_QUERY);

            resultSet.next();
            organization.setId( UUID.fromString(resultSet.getString(COLUMN_NAMES[0])) );

            logger.info("   >> Organization " + COLUMN_NAMES[0] + " >" + resultSet.getString(COLUMN_NAMES[0]) );

            organization.setName(resultSet.getString(COLUMN_NAMES[1]));

            logger.info("   >> Organization " + COLUMN_NAMES[1] + " >" + resultSet.getString(COLUMN_NAMES[1]) );

            organization.setPhysicalAddress(resultSet.getString(COLUMN_NAMES[2]));

            logger.info("   >> Organization " + COLUMN_NAMES[2] + " >" + resultSet.getString(COLUMN_NAMES[2]) );

            organization.setLegalAddress(resultSet.getString(COLUMN_NAMES[3]));

            logger.info("   >> Organization " + COLUMN_NAMES[3] + " >" + resultSet.getString(COLUMN_NAMES[3]) );

            return organization;

        } catch (SQLException e) {

            logger.error("error during fulfilling organization entity",e);
            logger.error(e.getStackTrace());

            return null;
        }
    }

    @Override
    public UUID put(Organization organization) {
        logger.info("start");

        try (Connection connection = connectorToRepo.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(PUT_QUERY)){

            UUID id = UUID.randomUUID();

            preparedStatement.setObject(1, id, Types.OTHER);
            preparedStatement.setString(2, organization.getName());
            preparedStatement.setString(3, organization.getPhysicalAddress());
            preparedStatement.setString(4, organization.getLegalAddress());

            preparedStatement.execute();
            return id;

        } catch (SQLException e) {
            logger.error("error during saving organization entity", e);
            return null;
        }
    }

    @Override
    public boolean update(Organization organization) {
        logger.info("start");

        try (Connection connection = connectorToRepo.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(POST_QUERY)){

            preparedStatement.setString(1, organization.getName());
            preparedStatement.setString(2, organization.getPhysicalAddress());
            preparedStatement.setString(3, organization.getLegalAddress());
            preparedStatement.setObject(4, organization.getId(), Types.OTHER);
            preparedStatement.execute();
            return true;

        } catch (SQLException e) {
            logger.error("error during saving organization entity", e);
            return false;
        }
    }

    @Override
    public boolean delete(UUID id) {
        logger.info("start");

        try (Connection connection = connectorToRepo.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_QUERY)){

            preparedStatement.setObject(1, id, Types.OTHER);
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {

            logger.error("error deleting entity", e);
            logger.error(e.getStackTrace());
            return false;
        }
    }

}
