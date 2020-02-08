package main.repository;

public class ErrorRepository extends AbstractRepository<String>{

    private static ErrorRepository instance;

    ErrorRepository() throws Exception {}

    public static ErrorRepository getInstance(){
        if(instance == null) {
            try {
                instance = new ErrorRepository();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return instance;
    }
}
