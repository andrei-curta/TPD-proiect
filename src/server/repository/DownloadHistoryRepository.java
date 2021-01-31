package server.repository;

import entities.DownloadHistoryEntity;
import entities.FileEntity;
import entities.FileVersionEntity;
import entities.UserEntity;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.type.LongType;
import util.HibernateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DownloadHistoryRepository extends BaseRepository<DownloadHistoryEntity> {
    public DownloadHistoryRepository() {
        super(DownloadHistoryEntity.class);
    }

    public boolean canDownloadFile(UserEntity user, FileEntity file) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();

            String sql = "select id from download_history where user_id = (?1) and file_version_id in (?2) ";
            List<Long> fileVersionIDs = file.getFileVersionsById().stream().map(FileVersionEntity::getId).collect(Collectors.toList());

            Query query = (Query) session.createNativeQuery(sql)
                    .setParameter(1, user.getId())
                    .setParameterList(2, fileVersionIDs.toArray());


            List<Long> res = query.list();
            session.close();

            if (res != null && res.size() > 0) {
                //if the user already dowloaded the file, it cannot be downloaded again
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
