package com.virtualhub.service;

import com.virtualhub.model.Carrito;
import com.virtualhub.model.Usuario;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    public byte[] generarFactura(List<Carrito> items, Usuario usuario) throws Exception {

        File file = ResourceUtils.getFile("classpath:reports/factura_virtualhub.jrxml");
        JasperReport jasperReport =
                JasperCompileManager.compileReport(file.getAbsolutePath());

        JRBeanCollectionDataSource dataSource =
                new JRBeanCollectionDataSource(items);

        double total = items.stream()
                .mapToDouble(i -> i.getJuego().getPrecio())
                .sum();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("usuarioNombre", usuario.getNombre());
        parameters.put("usuarioEmail", usuario.getEmail());
        parameters.put("saldoRestante", usuario.getSaldo());
        parameters.put("totalCompra", total);

        JasperPrint jasperPrint = JasperFillManager.fillReport(
                jasperReport,
                parameters,
                dataSource
        );

        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}