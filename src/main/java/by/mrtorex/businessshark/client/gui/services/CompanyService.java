package by.mrtorex.businessshark.client.gui.services;

import by.mrtorex.businessshark.server.enums.Operation;
import by.mrtorex.businessshark.server.exceptions.ResponseException;
import by.mrtorex.businessshark.server.model.entities.Company;
import by.mrtorex.businessshark.server.network.Request;
import by.mrtorex.businessshark.server.network.Response;
import by.mrtorex.businessshark.server.network.ServerClient;
import by.mrtorex.businessshark.server.serializer.Serializer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Сервис для взаимодействия с сервером по операциям с компаниями.
 */
public class CompanyService {
    private static final Logger logger = LogManager.getLogger(CompanyService.class);
    private final ServerClient serverClient;

    /**
     * Конструктор по умолчанию.
     */
    public CompanyService() {
        this.serverClient = ServerClient.getInstance();
        logger.info("Инициализирован CompanyService с клиентом сервера");
    }

    /**
     * Получает список всех компаний.
     *
     * @return ответ сервера с данными о компаниях
     */
    public Response getAll() {
        try {
            Response response = serverClient.sendRequest(new Request(Operation.GET_ALL_COMPANIES));
            logger.info("Запрошен список всех компаний");
            return response;
        } catch (Exception e) {
            logger.error("Ошибка при получении списка компаний", e);
            return new Response(false, "Ошибка при получении списка компаний", null);
        }
    }

    /**
     * Удаляет компанию по её идентификатору.
     *
     * @param company компания для удаления
     * @return ответ сервера с результатом операции
     * @throws IllegalArgumentException если компания или её ID равны null
     */
    public Response delCompany(Company company) {
        try {
            if (company == null || company.getId() == null) {
                logger.warn("Попытка удаления компании с null параметрами");
                throw new IllegalArgumentException("Компания или её ID не могут быть null");
            }
            String companyIdJson = Serializer.toJson(company.getId());
            Response response = serverClient.sendRequest(new Request(Operation.DELETE_COMPANY, companyIdJson));
            logger.info("Удалена компания с ID: {}", company.getId());
            return response;
        } catch (IllegalArgumentException e) {
            logger.error("Некорректные данные для удаления компании", e);
            return new Response(false, e.getMessage(), null);
        } catch (ResponseException e) {
            logger.error("Ошибка удаления компании: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    /**
     * Обновляет данные компании.
     *
     * @param company компания с обновлёнными данными
     * @return ответ сервера с результатом операции
     * @throws IllegalArgumentException если компания равна null
     */
    public Response updateCompany(Company company) {
        try {
            if (company == null) {
                logger.warn("Попытка обновления компании с null параметром");
                throw new IllegalArgumentException("Компания не может быть null");
            }
            String companyJson = Serializer.toJson(company);
            Response response = serverClient.sendRequest(new Request(Operation.UPDATE_COMPANY, companyJson));
            logger.info("Обновлены данные компании с ID: {}", company.getId());
            return response;
        } catch (IllegalArgumentException e) {
            logger.error("Некорректные данные для обновления компании", e);
            return new Response(false, e.getMessage(), null);
        } catch (ResponseException e) {
            logger.error("Ошибка обновления компании: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    /**
     * Добавляет новую компанию.
     *
     * @param company новая компания
     * @return ответ сервера с результатом операции
     * @throws IllegalArgumentException если компания равна null
     */
    public Response addCompany(Company company) {
        try {
            if (company == null) {
                logger.warn("Попытка создания компании с null параметром");
                throw new IllegalArgumentException("Компания не может быть null");
            }
            String companyJson = Serializer.toJson(company);
            Response response = serverClient.sendRequest(new Request(Operation.CREATE_COMPANY, companyJson));
            logger.info("Создана новая компания: {}", company.getName());
            return response;
        } catch (IllegalArgumentException e) {
            logger.error("Некорректные данные для создания компании", e);
            return new Response(false, e.getMessage(), null);
        } catch (ResponseException e) {
            logger.error("Ошибка создания компании: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    /**
     * Получает список акций, связанных с компанией.
     *
     * @param company компания, для которой запрашиваются акции
     * @return ответ сервера с данными об акциях
     * @throws IllegalArgumentException если компания равна null
     */
    public Response getCompanyStocks(Company company) {
        try {
            if (company == null) {
                logger.warn("Попытка получения акций для null компании");
                throw new IllegalArgumentException("Компания не может быть null");
            }
            String companyJson = Serializer.toJson(company);
            Response response = serverClient.sendRequest(new Request(Operation.GET_STOCKS_BY_COMPANY, companyJson));
            logger.info("Запрошены акции для компании с ID: {}", company.getId());
            return response;
        } catch (IllegalArgumentException e) {
            logger.error("Некорректные данные для получения акций компании", e);
            return new Response(false, e.getMessage(), null);
        } catch (ResponseException e) {
            logger.error("Ошибка получения акций компании: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }
}