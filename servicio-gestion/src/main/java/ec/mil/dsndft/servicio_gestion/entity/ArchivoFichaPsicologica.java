package ec.mil.dsndft.servicio_gestion.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "archivos_ficha_psicologica")
public class ArchivoFichaPsicologica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ficha_id", nullable = false)
    private FichaPsicologica fichaPsicologica;

    @Column(name = "nombre_archivo", nullable = false)
    private String nombreArchivo;

    @Column(name = "ruta_ftp", nullable = false)
    private String rutaFtp;

    @Column(name = "fecha_subida", nullable = false)
    private LocalDateTime fechaSubida;

    public ArchivoFichaPsicologica() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public FichaPsicologica getFichaPsicologica() { return fichaPsicologica; }
    public void setFichaPsicologica(FichaPsicologica fichaPsicologica) { this.fichaPsicologica = fichaPsicologica; }

    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }

    public String getRutaFtp() { return rutaFtp; }
    public void setRutaFtp(String rutaFtp) { this.rutaFtp = rutaFtp; }

    public LocalDateTime getFechaSubida() { return fechaSubida; }
    public void setFechaSubida(LocalDateTime fechaSubida) { this.fechaSubida = fechaSubida; }
}
