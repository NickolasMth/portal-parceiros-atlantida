
INSERT INTO patrocinador (
    nome_fantasia,
    razao_social,
    cnpj,
    email_contato,
    telefone,
    website,
    endereco,
    ativo
) VALUES
      (
          'Cliente Amarelo',
          'Cliente Amarelo S.A.',
          '12.345.678/0001-90',
          'contato@clienteamarelo.example',
          '(51) 3333-1000',
          'https://clienteamarelo.example',
          '{
            "logradouro": "Rua das Acácias",
            "numero": "150",
            "complemento": "Sala 301",
            "bairro": "Centro",
            "cidade": "Porto Alegre",
            "estado": "RS",
            "cep": "90010-100",
            "pais": "Brasil"
          }'::jsonb,
          TRUE
      ),
      (
          'Cliente Roxo',
          'Cliente Roxo Ltda.',
          '23.456.789/0001-01',
          'contato@clienteroxo.example',
          '(51) 3333-2000',
          'https://clienteroxo.example',
          '{
            "logradouro": "Avenida Independência",
            "numero": "820",
            "complemento": "Conjunto 402",
            "bairro": "Independência",
            "cidade": "Porto Alegre",
            "estado": "RS",
            "cep": "90035-070",
            "pais": "Brasil"
          }'::jsonb,
          TRUE
      ),
      (
          'Cliente Verde',
          'Cliente Verde Ltda.',
          '34.567.890/0001-12',
          'contato@clienteverde.example',
          '(51) 3333-3000',
          'https://clienteverde.example',
          '{
            "logradouro": "Rua dos Ipês",
            "numero": "245",
            "complemento": "Bloco B",
            "bairro": "Moinhos de Vento",
            "cidade": "Porto Alegre",
            "estado": "RS",
            "cep": "90570-020",
            "pais": "Brasil"
          }'::jsonb,
          TRUE
);


INSERT INTO patrocinio (
    cota_id,
    status,
    valor_acordado,
    evento_id,
    patrocinador_id
)
SELECT
    c.id,
    'ATIVO',
    c.valor,
    e.id,
    p.id
FROM patrocinador p
         JOIN cota c ON (
    (p.nome_fantasia = 'Cliente Amarelo' AND c.codigo = 'MASTER')
        OR
    (p.nome_fantasia = 'Cliente Roxo' AND c.codigo = 'SUBCOTA_G')
        OR
    (p.nome_fantasia = 'Cliente Verde' AND c.codigo = 'SUBCOTA')
    )
         CROSS JOIN evento e
WHERE e.edicao = 2027;

