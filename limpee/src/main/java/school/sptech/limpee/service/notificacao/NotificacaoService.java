package school.sptech.limpee.service.notificacao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import school.sptech.limpee.api.repository.notificacao.NotificacaoRepository;
import school.sptech.limpee.domain.FormularioServico.FormularioServico;
import school.sptech.limpee.domain.notificacao.Notificacao;
import school.sptech.limpee.service.notificacao.dto.NotificacaoClienteDto;
import school.sptech.limpee.service.notificacao.dto.NotificacaoDto;
import school.sptech.limpee.service.notificacao.dto.NotificacaoMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NotificacaoService {
    @Autowired
    private NotificacaoRepository notificacaoRepository;

    public List<NotificacaoDto> buscarNotificacoes(long id) {

        List<Notificacao> notificacoes = notificacaoRepository.findAllByIdUsuario(id);

        if (notificacoes.isEmpty())
            return new ArrayList<>();

        notificacoes = notificacoes.stream().filter(notificacao -> !notificacao.isAprovadoPrestador()).toList();

        return notificacoes.stream().map(NotificacaoMapper::of).toList();
    }

    public List<NotificacaoClienteDto> buscarNotificacoesCliente(long id) {

        List<Notificacao> notificacoes = notificacaoRepository.findAllByIdCliente(id);

        if (notificacoes.isEmpty())
            return new ArrayList<>();

        notificacoes = notificacoes.stream().filter(Notificacao::isAprovadoPrestador).toList();

        return notificacoes.stream().map(NotificacaoMapper::mapToClienteDto).toList();
    }

    public static List<String> getAllTrue(FormularioServico form) {
        List<String> camposTrue = new ArrayList<>();

        if (form.hasArmario())
            camposTrue.add("armario");
        if (form.hasAreaExterna())
            camposTrue.add("areaExterna");
        if (form.hasGeladeira())
            camposTrue.add("geladeira");
        if (form.hasJanelas())
            camposTrue.add("janelas");
        if (form.getLavarRoupa())
            camposTrue.add("lavarRoupa");
        if (form.getPassarRoupa())
            camposTrue.add("passarRoupa");

        return camposTrue;
    }

    public void aprovarNotificacao(long idNotificacao, boolean aprovado, double valorOrcamento) {

        if (valorOrcamento <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O valor do orçamento deve ser maior que zero.");

        Optional<Notificacao> notificacao = notificacaoRepository.findById(idNotificacao);

        if (notificacao.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Não foi encontrada nenhuma notificação com o ID especificado");

        if (!aprovado)
            notificacaoRepository.delete(notificacao.get());

        notificacao.get().setValorOrcamento(valorOrcamento);
        notificacao.get().setAprovadoPrestador(true);

        notificacaoRepository.save(notificacao.get());
    }
    public void aprovarNotificacaoCliente(long idNotificacao, boolean aprovado) {

        Optional<Notificacao> notificacao = notificacaoRepository.findById(idNotificacao);

        if (notificacao.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Não foi encontrada nenhuma notificação com o ID especificado");

        if (!aprovado)
            notificacaoRepository.delete(notificacao.get());


        notificacao.get().setAprovadoCliente(true);

        notificacaoRepository.save(notificacao.get());
    }
}
