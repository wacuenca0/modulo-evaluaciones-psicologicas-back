package ec.mil.dsndft.servicio_gestion.service.support;

import ec.mil.dsndft.servicio_gestion.entity.CatalogoDiagnosticoCie10;
import ec.mil.dsndft.servicio_gestion.entity.FichaPsicologica;
import ec.mil.dsndft.servicio_gestion.model.enums.CondicionClinicaEnum;
import ec.mil.dsndft.servicio_gestion.model.enums.FrecuenciaSeguimientoEnum;
import ec.mil.dsndft.servicio_gestion.model.enums.TipoSesionEnum;
import ec.mil.dsndft.servicio_gestion.model.value.DiagnosticoCie10;
import ec.mil.dsndft.servicio_gestion.model.value.PlanSeguimiento;
import org.springframework.stereotype.Component;

@Component
public class FichaCondicionManager {

    public void aplicarCondicionObligatoria(FichaPsicologica ficha,
                                            CondicionClinicaEnum condicion,
                                            CatalogoDiagnosticoCie10 diagnosticoCatalogo,
                                            String cieCodigo,
                                            String cieNombre,
                                            String cieCategoriaPadre,
                                            Integer cieNivel,
                                            String cieDescripcion,
                                            String planFrecuenciaRaw,
                                            String planTipoSesionRaw,
                                            String planDetalleRaw) {
        aplicarCondicion(ficha, condicion, diagnosticoCatalogo, cieCodigo, cieNombre, cieCategoriaPadre, cieNivel, cieDescripcion, planFrecuenciaRaw, planTipoSesionRaw, planDetalleRaw, true);
    }

    public void aplicarCondicionOpcional(FichaPsicologica ficha,
                                         CondicionClinicaEnum condicion,
                                         CatalogoDiagnosticoCie10 diagnosticoCatalogo,
                                         String cieCodigo,
                                         String cieNombre,
                                         String cieCategoriaPadre,
                                         Integer cieNivel,
                                         String cieDescripcion,
                                         String planFrecuenciaRaw,
                                         String planTipoSesionRaw,
                                         String planDetalleRaw) {
        if (!hasAnyValue(condicion != null ? condicion.getCanonical() : null,
                cieCodigo,
                cieNombre,
                cieCategoriaPadre,
                cieDescripcion,
                planFrecuenciaRaw,
                planTipoSesionRaw,
                planDetalleRaw)
            && cieNivel == null
            && diagnosticoCatalogo == null) {
            return;
        }
        aplicarCondicion(ficha, condicion, diagnosticoCatalogo, cieCodigo, cieNombre, cieCategoriaPadre, cieNivel, cieDescripcion, planFrecuenciaRaw, planTipoSesionRaw, planDetalleRaw, false);
    }

    private void aplicarCondicion(FichaPsicologica ficha,
                                  CondicionClinicaEnum condicionSolicitada,
                                  CatalogoDiagnosticoCie10 diagnosticoCatalogo,
                                  String cieCodigo,
                                  String cieNombre,
                                  String cieCategoriaPadre,
                                  Integer cieNivel,
                                  String cieDescripcion,
                                  String planFrecuenciaRaw,
                                  String planTipoSesionRaw,
                                  String planDetalleRaw,
                                  boolean condicionObligatoria) {
        // Ya no se asigna condición ni diagnósticos en la creación básica de ficha.
        // Esta lógica solo se usa cuando se actualiza la condición clínica explícitamente.
        // No hacer nada aquí para la creación básica.
    }

    private CondicionClinicaEnum resolveCondicion(FichaPsicologica ficha, CondicionClinicaEnum condicionSolicitada, boolean condicionObligatoria) {
        if (condicionSolicitada == null) {
            if (condicionObligatoria) {
                throw new IllegalArgumentException("La condicion clinica es obligatoria");
            }
            CondicionClinicaEnum existente = ficha.getCondicionClinica();
            if (existente == null) {
                throw new IllegalArgumentException("La ficha no tiene condicion clinica previa para reutilizar");
            }
            return existente;
        }
        return condicionSolicitada;
    }

    private DiagnosticoCie10 buildDiagnostico(FichaPsicologica ficha,
                                              CatalogoDiagnosticoCie10 diagnosticoCatalogo,
                                              String cieCodigo,
                                              String cieNombre,
                                              String cieCategoriaPadre,
                                              Integer cieNivel,
                                              String cieDescripcion,
                                              boolean condicionObligatoria) {
        String codigo = null;
        String nombre = null;
        String categoriaPadre = null;
        Integer nivel = null;
        String descripcion = null;

        if (diagnosticoCatalogo != null) {
            codigo = uppercase(trim(diagnosticoCatalogo.getCodigo()));
            nombre = trim(diagnosticoCatalogo.getNombre());
            categoriaPadre = uppercase(trim(diagnosticoCatalogo.getCategoriaPadre()));
            nivel = diagnosticoCatalogo.getNivel();
            descripcion = trim(diagnosticoCatalogo.getDescripcion());
        }

        // Eliminada lógica de diagnóstico existente (obsoleto)

        String codigoSolicitud = uppercase(trim(cieCodigo));
        if (codigo == null && codigoSolicitud != null) {
            codigo = codigoSolicitud;
        }

        String nombreSolicitud = trim(cieNombre);
        if (nombreSolicitud != null) {
            nombre = nombreSolicitud;
        }

        String categoriaSolicitud = uppercase(trim(cieCategoriaPadre));
        if (categoriaSolicitud != null) {
            categoriaPadre = categoriaSolicitud;
        }

        if (cieNivel != null) {
            if (cieNivel < 0) {
                throw new IllegalArgumentException("El nivel del diagnóstico debe ser mayor o igual a 0");
            }
            nivel = cieNivel;
        }

        String descripcionSolicitud = trim(cieDescripcion);
        if (descripcionSolicitud != null) {
            descripcion = descripcionSolicitud;
        }

        if (condicionObligatoria && diagnosticoCatalogo == null) {
            throw new IllegalArgumentException("Debe seleccionar un diagnostico del catalogo CIE-10 para la condicion clinica");
        }

        if (condicionObligatoria && codigo == null) {
            throw new IllegalArgumentException("El codigo CIE-10 es obligatorio para la condicion seleccionada");
        }

        if (codigo == null && descripcion == null && nombre == null) {
            return null;
        }

        return DiagnosticoCie10.builder()
            .codigo(codigo)
            .nombre(nombre)
            .categoriaPadre(categoriaPadre)
            .nivel(nivel)
            .descripcion(descripcion)
            .build();
    }

    private CatalogoDiagnosticoCie10 resolveDiagnosticoCatalogo(FichaPsicologica ficha,
                                                                CatalogoDiagnosticoCie10 diagnosticoCatalogo,
                                                                boolean condicionObligatoria) {
        // Adaptación: tomar el primer diagnóstico de la lista si existe
        CatalogoDiagnosticoCie10 existente = (ficha.getDiagnosticosCie10() != null && !ficha.getDiagnosticosCie10().isEmpty()) ? ficha.getDiagnosticosCie10().get(0) : null;
        CatalogoDiagnosticoCie10 seleccionado = diagnosticoCatalogo != null ? diagnosticoCatalogo : existente;
        if (condicionObligatoria && seleccionado == null) {
            throw new IllegalArgumentException("Debe seleccionar un diagnostico del catalogo CIE-10");
        }
        return seleccionado;
    }

    private PlanSeguimiento buildPlan(FichaPsicologica ficha,
                                      CondicionClinicaEnum condicion,
                                      String planFrecuenciaRaw,
                                      String planTipoSesionRaw,
                                      String planDetalleRaw,
                                      boolean condicionObligatoria) {
        String frecuenciaToken = trim(planFrecuenciaRaw);
        String tipoSesionToken = trim(planTipoSesionRaw);
        String detalle = trim(planDetalleRaw);

        PlanSeguimiento existente = ficha.getPlanSeguimiento();

        if (frecuenciaToken == null && existente != null && existente.getFrecuencia() != null) {
            frecuenciaToken = existente.getFrecuencia().getCanonical();
        }

        if (tipoSesionToken == null && existente != null && existente.getTipoSesion() != null) {
            tipoSesionToken = existente.getTipoSesion().getCanonical();
        }

        if (detalle == null && existente != null) {
            detalle = existente.getDetalle();
        }

        // Para SEGUIMIENTO, frecuencia y tipo de sesión son obligatorios
        // Para TRANSFERENCIA, el plan es opcional (puede ser null)
        if (condicion == CondicionClinicaEnum.SEGUIMIENTO) {
            if (frecuenciaToken == null) {
                throw new IllegalArgumentException("La frecuencia del plan de seguimiento es obligatoria");
            }
            if (tipoSesionToken == null) {
                throw new IllegalArgumentException("El tipo de sesion es obligatorio");
            }
        } else if (condicion == CondicionClinicaEnum.TRANSFERENCIA) {
            // Para transferencia, si no hay frecuencia o tipo de sesión, no creamos plan
            if (frecuenciaToken == null || tipoSesionToken == null) {
                return null;
            }
        }

        return PlanSeguimiento.builder()
            .frecuencia(FrecuenciaSeguimientoEnum.from(frecuenciaToken)
                .orElseThrow(() -> new IllegalArgumentException("Frecuencia de seguimiento no soportada: " + planFrecuenciaRaw)))
            .tipoSesion(TipoSesionEnum.from(tipoSesionToken)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de sesion no soportado: " + planTipoSesionRaw)))
            .detalle(detalle)
            .build();
    }

    private String trim(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String uppercase(String value) {
        return value != null ? value.toUpperCase() : null;
    }

    private boolean hasAnyValue(String... values) {
        if (values == null) {
            return false;
        }
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
