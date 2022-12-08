package dev.sunbirdrc.consent.controller;

import dev.sunbirdrc.consent.entity.Consent;
import dev.sunbirdrc.consent.exceptions.ConsentDefinitionNotFoundException;
import dev.sunbirdrc.consent.service.ConsentService;
import dev.sunbirdrc.pojos.dto.ConsentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class ConsentController {

    private static final String STATUS = "status";
    @Autowired
    private ConsentService consentService;

    @PostMapping("/api/v1/consent")
    public ResponseEntity<Consent> createConsent(@RequestBody ConsentDTO consentDTO) {
        Consent consent = Consent.fromDTO(consentDTO);
        Consent savedConsent = consentService.saveConsent(consent);
        return new ResponseEntity<>(savedConsent, HttpStatus.CREATED);
    }

    @GetMapping(value = "/api/v1/consent/{id}")
    public ResponseEntity<Consent> getConsentById(@PathVariable String id) throws ConsentDefinitionNotFoundException {
        Consent consent = consentService.retrieveConsents(id);
        return new ResponseEntity<>(consent, HttpStatus.OK);
    }

    @PutMapping(value = "/api/v1/consent/{id}")
    public ResponseEntity<Consent> grantOrDenyConsent(@PathVariable String id, @RequestBody Map<String, String> statusMap) {
        try {
            Consent consent = consentService.grantOrDenyConsent(statusMap.get(STATUS), id);
            return new ResponseEntity<>(consent, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/api/v1/consent/owner/{ownerId}")
    public ResponseEntity<List<Consent>> getConsentByOwnerId(@PathVariable String ownerId) {
        List<Consent> consent = consentService.retrieveConsentByOwnerId(ownerId);
        return new ResponseEntity<>(consent, HttpStatus.OK);
    }
}
